//
//  AppUtils.m
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "AppUtils.h"

@implementation AppUtils
@synthesize activityIndicator;

static NSInteger mEnteredChatting = 0;
static SocketIOClient *mSocket;
static SocketManager *socketManager;

+(AppUtils*)sharedInstance{
    static dispatch_once_t onceToken;
    static AppUtils* instance = nil;
    dispatch_once(&onceToken, ^{
        instance = [[AppUtils alloc] init];
    });
    return instance;
}

+ (void)addActivityIndicator :(UIView *) parentView
{
    if ([self sharedInstance].activityIndicator == nil) {
        CGRect screenRect = [[UIScreen mainScreen] bounds];
        
        [self sharedInstance].activityIndicator = [[WNAActivityIndicator alloc] initWithFrame:screenRect];
        [[self sharedInstance].activityIndicator setHidden:NO];
    }
    
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
    if (![[self sharedInstance].activityIndicator isDescendantOfView:parentView]) {
        [parentView addSubview:[self sharedInstance].activityIndicator];
    }
}

+ (void)removeActivityIndicator :(UIView *) parentView
{
    [[self sharedInstance].activityIndicator setHidden:YES];
    [[self sharedInstance].activityIndicator removeFromSuperview];
    [self sharedInstance].activityIndicator = nil;
    
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
}

+(BOOL)isTextEmpty :(NSString *) aString{
    if ((NSNull *) aString == [NSNull null]) {
        return YES;
    }
    
    if (aString == nil) {
        return YES;
    } else if ([aString length] == 0) {
        return YES;
    } else {
        aString = [aString stringByTrimmingCharactersInSet: [NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if ([aString length] == 0) {
            return YES;
        }
    }
    
    return NO;  
}
+(BOOL)isValidEmail:(NSString *)checkString
{
    BOOL stricterFilter = NO; // Discussion http://blog.logichigh.com/2010/09/02/validating-an-e-mail-address/
    NSString *stricterFilterString = @"^[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
    NSString *laxString = @"^.+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2}[A-Za-z]*$";
    NSString *emailRegex = stricterFilter ? stricterFilterString : laxString;
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
    return [emailTest evaluateWithObject:checkString];
}

+(void)saveUserInfo:(id)responseObject
{
    NSDictionary *receivedData = responseObject;
    NSString *token = [receivedData objectForKey:@"token"];
    NSDictionary *dataList = [receivedData objectForKey:@"user"];
    NSInteger  userId = [[dataList objectForKey:@"id"] integerValue];
    NSData *data = [NSKeyedArchiver archivedDataWithRootObject:dataList];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:data
                 forKey:	USER_OBJECT_STRING];
    [defaults setObject:token forKey:USER_TOKEN];
    [defaults setBool:YES
               forKey:IS_LOGED_IN];
    [defaults setBool:YES
               forKey:NOT_FIRST_TIME];
    [defaults setInteger:userId forKey:USER_ID];
    [defaults synchronize];
}
+(void)saveUserAccountInfo:(NSDictionary *)dict
{
    NSData *data = [NSKeyedArchiver archivedDataWithRootObject:dict];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:data
                 forKey:    USER_OBJECT_STRING];

    [defaults synchronize];
}
+(NSString *)getUserToken
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *token = [defaults stringForKey:USER_TOKEN];
    return token;
}

+(BOOL)isLoggedIn
{
    BOOL isLogIn = NO;
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    isLogIn = [defaults boolForKey:IS_LOGED_IN];
    return isLogIn;
}
+(void)setLogOut
{
    BOOL isLogIn = NO;
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    isLogIn = [defaults boolForKey:IS_LOGED_IN];
    [defaults setBool:NO
               forKey:IS_LOGED_IN];

}
+(BOOL)isNotFirstTime
{
    BOOL isFirstTime = NO;
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    isFirstTime = [defaults boolForKey:NOT_FIRST_TIME];
    return isFirstTime;
}

+(void)setNotFirstTime
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:YES
               forKey:NOT_FIRST_TIME];
}

+(NSDictionary *)getUserInfo
{
    NSUserDefaults *defaults =[NSUserDefaults standardUserDefaults];
    NSData *dicData = [defaults objectForKey:USER_OBJECT_STRING];
    return  [NSKeyedUnarchiver unarchiveObjectWithData:dicData];
    
}
+(NSInteger )getUserId
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults integerForKey:USER_ID];
}
+(NSString *)getUserImageUrl
{
    NSDictionary *userInfo = [AppUtils getUserInfo];
    NSNumber *ids =  [userInfo objectForKey:@"photo"];
    NSString *idstr = [NSString stringWithFormat:@"%@",ids];
    NSString *url = [[BASE_URL stringByAppendingString:IMAGE_URL] stringByAppendingString:idstr];
    return url;
}

+(NSString *)getUserName
{
    NSDictionary *userInfo = [AppUtils getUserInfo];
    NSString *first_name =  [userInfo objectForKey:@"first_name"];
    NSString *last_name =  [userInfo objectForKey:@"last_name"];
    NSString *userName = [[first_name stringByAppendingString:@" "] stringByAppendingString: last_name];

    return userName;
}
+(CLLocation *)getUserLocation
{
    NSDictionary *userInfo = [AppUtils getUserInfo];
    id latObject =[userInfo objectForKey:@"geo_lat"];
    id lngObject = [userInfo objectForKey:@"geo_lng"];
    double lat = 0 , lng = 0;
    if ((NSNull *)latObject != [NSNull null] && (NSNull *)lngObject != [NSNull null])
    {
        lat = [latObject doubleValue];
        lng = [lngObject doubleValue];
    }

    CLLocation *location = [[CLLocation alloc] initWithLatitude:lat longitude:lng];
    return location;
}

+(void )saveUserLocation:(CLLocation *)loc
{
    double lat = loc.coordinate.latitude;
    double lng = loc.coordinate.longitude;
    
    NSString *latString = [NSString stringWithFormat:@"%f",lat ];
    NSString *lngString = [NSString stringWithFormat:@"%f",lng];
    
    NSMutableDictionary *userInfo = (NSMutableDictionary *)[AppUtils getUserInfo];
    
    [userInfo setValue:latString
             forKey:@"geo_lat"];
    [userInfo setValue:lngString
             forKey:@"geo_lng"];
    
    NSData *data = [NSKeyedArchiver archivedDataWithRootObject:userInfo];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:data
                 forKey:USER_OBJECT_STRING];
}

+(CLLocation *)getLocationLocation
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    id latObject =[defaults objectForKey:LOCATION_LAT];
    id lngObject = [defaults objectForKey:LOCATION_LNG];
    double lat = 0 , lng = 0;
    if ((NSNull *)latObject != [NSNull null] && (NSNull *)lngObject != [NSNull null])
    {
        lat = [latObject doubleValue];
        lng = [lngObject doubleValue];
    }
    
    
    return [[CLLocation alloc] initWithLatitude:lat
                                      longitude:lng];
}
+(void) setLocationLocation:(CLLocation *) loc
{
    double lat = loc.coordinate.latitude;
    double lng = loc.coordinate.longitude;
    NSNumber *latNum = [NSNumber numberWithDouble:lat];
    NSNumber *lngNum = [NSNumber numberWithDouble:lng];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    [defaults setObject:latNum forKey:LOCATION_LAT];
    [defaults setObject:lngNum
                 forKey:LOCATION_LNG];
    
}
+(CLLocation *)getServiceLocation
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    id latObject =[defaults objectForKey:LOCATION_SERVICE_LAT];
    id lngObject = [defaults objectForKey:LOCATION_SERVICE_LNG];
    double lat = 0 , lng = 0;
    if ((NSNull *)latObject != [NSNull null] && (NSNull *)lngObject != [NSNull null])
    {
        lat = [latObject doubleValue];
        lng = [lngObject doubleValue];
    }
    
    
    return [[CLLocation alloc] initWithLatitude:lat
                                      longitude:lng];
}
+(void) setServiceLocation:(CLLocation *) loc
{
    double lat = loc.coordinate.latitude;
    double lng = loc.coordinate.longitude;
    NSNumber *latNum = [NSNumber numberWithDouble:lat];
    NSNumber *lngNum = [NSNumber numberWithDouble:lng];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    [defaults setObject:latNum forKey:LOCATION_SERVICE_LAT];
    [defaults setObject:lngNum
                 forKey:LOCATION_SERVICE_LNG];
    
}
+(NSString *)displayCurrencyInfoForLocale
{
    NSNumberFormatter *formatter = [[NSNumberFormatter alloc] init];
    [formatter setNumberStyle:NSNumberFormatterCurrencyStyle];
    NSLocale *locale =  [NSLocale currentLocale];
    
    NSString *currency = [locale objectForKey: NSLocaleCurrencySymbol];
    
    return currency;
}
+(NSString *)getStringFromDictionary:(NSDictionary *)dict objectForKey:(NSString *)key
{
    NSString *string = [dict objectForKey:key];
    if ((string == (NSString *)[NSNull null]) || string == nil)
        return @"";
    
    return string;
}
+(UIColor*)colorWithHexString:(NSString*)hex
{
    NSString *cString = [[hex stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] uppercaseString];
    
    // String should be 6 or 8 characters
    if ([cString length] < 6) return [UIColor grayColor];
    
    // strip 0X if it appears
    if ([cString hasPrefix:@"0X"]) cString = [cString substringFromIndex:2];
    
    if ([cString length] != 6) return  [UIColor grayColor];
    
    // Separate into r, g, b substrings
    NSRange range;
    range.location = 0;
    range.length = 2;
    NSString *rString = [cString substringWithRange:range];
    
    range.location = 2;
    NSString *gString = [cString substringWithRange:range];
    
    range.location = 4;
    NSString *bString = [cString substringWithRange:range];
    
    // Scan values
    unsigned int r, g, b;
    [[NSScanner scannerWithString:rString] scanHexInt:&r];
    [[NSScanner scannerWithString:gString] scanHexInt:&g];
    [[NSScanner scannerWithString:bString] scanHexInt:&b];
    
    return [UIColor colorWithRed:((float) r / 255.0f)
                           green:((float) g / 255.0f)
                            blue:((float) b / 255.0f)
                           alpha:1.0f];
}

+ (UIViewController *)currentTopViewController {
    UIViewController *topVC = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    while (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    return topVC;
}
+ (UIImage*)rotateUIImage:(UIImage*)sourceImage clockwise:(BOOL)clockwise
{
    CGSize size = sourceImage.size;
    UIGraphicsBeginImageContext(CGSizeMake(size.height, size.width));
    [[UIImage imageWithCGImage:[sourceImage CGImage] scale:1.0 orientation:clockwise ? UIImageOrientationRight : UIImageOrientationLeft] drawInRect:CGRectMake(0,0,size.height ,size.width)];
    UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return newImage;
}

+ (UIImage *) rotate: (UIImage *) image
{
    double angle = 90;
    CGSize s = {image.size.width, image.size.height};
    UIGraphicsBeginImageContext(s);
    CGContextRef ctx = UIGraphicsGetCurrentContext();
    CGContextTranslateCTM(ctx, 0,image.size.height);
    CGContextScaleCTM(ctx, 1.0, -1.0);
    
    CGContextRotateCTM(ctx, 2*M_PI*angle/360);
    CGContextDrawImage(ctx,CGRectMake(0,0,image.size.width, image.size.height),image.CGImage);
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
    
}
+(void)showMessageWithOk:(NSString*)message withTitle:(NSString *)title{
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertController *alertController = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
        [alertController addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            
        }]];
        
        [[AppUtils currentTopViewController] presentViewController:alertController animated:YES completion:^{
        }];
    });
}



+ (BOOL)chatting_flag {
    return chatting_flag;
}
+ (void)setchatting_flag:(BOOL)par {
    chatting_flag = par;
}


+(NSInteger )isAppRunning
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [[defaults objectForKey:@"is_running"] integerValue];
}
+(void)setAppRunning:(NSInteger)para
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setInteger:para                                  forKey:@"is_running"];
}

+(void)setNotifiUserData:(NSInteger)userId itemID:(NSInteger )nsItemId
fName:(NSString *)first_name lName:(NSString *)last_name uImage:(NSString *)userImage
{
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    
    [defaults setInteger:userId
                  forKey:chatting_user_id_string];
    [defaults setInteger:nsItemId
                  forKey:chatting_item_id_string];
    [defaults setObject:first_name
                 forKey:chatting_user_firstname_string];
    [defaults setObject:last_name
                 forKey:chatting_user_lastname_string];
    [defaults setObject:userImage
                 forKey:chatting_user_image_string];

}
+(ChatItemModel *)getNotifiUserData
{
    ChatItemModel *model = [[ChatItemModel alloc] init];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSInteger userId = [[defaults  objectForKey:chatting_user_id_string] integerValue];
    NSInteger itemId = [[defaults  objectForKey:chatting_item_id_string] integerValue];
    NSString *first_name = [defaults objectForKey:chatting_user_firstname_string];
    NSString *last_name = [defaults objectForKey:chatting_user_lastname_string];
    NSString *user_image = [defaults objectForKey:chatting_user_image_string];
    
    model.user_id = userId;
    model.itemId = itemId;
    model.user_first_name = first_name;
    model.user_last_name = last_name;
    model.user_photo_id = user_image;
    return model;
    
}

+(NSInteger)isChatPush
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSInteger isChat = [[defaults objectForKey:chatting_flag_string] integerValue];
    return isChat;
}
+(void)setChatPush:(NSInteger) para
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setInteger:para
                  forKey:chatting_flag_string];
}


+(void) setEnteredChatting:(BOOL)val{
    if (val == YES)
        mEnteredChatting = 1;
    else if (val == NO)
        mEnteredChatting = 0;
}
+(BOOL) getEnteredChatting{
    if (mEnteredChatting == 0)
        return NO;
    return YES;
}

+ (NSInteger) mEnteredChatting { return mEnteredChatting; }
+ (void) setMEnteredChatting:(NSInteger) value { mEnteredChatting = value; }
+ (SocketIOClient *) mSocket{return mSocket;}
+ (void) setMSocket:(SocketIOClient *)value{
    mSocket = value;    
}
+ (SocketManager *) socketManager{return socketManager;}
+ (void) setSocketManager:(SocketManager *)value{ socketManager = value;}

@end
