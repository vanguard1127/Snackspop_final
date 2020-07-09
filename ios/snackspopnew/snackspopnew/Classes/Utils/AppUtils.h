//
//  AppUtils.h
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WNAActivityIndicator.h"
#import "../Networking/AFAppAPIClient.h"
#import <CoreLocation/CoreLocation.h>
#import "../Models/ChatItemModel.h"
@import SocketIO;

typedef void (^disMissCallback)(NSInteger index);
#define SYSTEM_VERSION_GRATERTHAN_OR_EQUALTO(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedAscending)


#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]
NS_ASSUME_NONNULL_BEGIN
static NSString* const BASE_SITE_URL = @"http://13.58.163.46:3030";
static NSString *const BASE_URL = @"http://13.58.163.46:3030/snackspop/api/";
//static NSString* const BASE_SITE_URL = @"http://192.168.1.102:3030";
//static NSString *const BASE_URL = @"http://192.168.1.102:3030/snackspop/api/";
static NSString *const IMAGE_URL = @"files/";
static NSString *const GET_ITEMS = @"items/get_items/";
static NSString *const LOGIN = @"users/signin";
static NSString *const SIGNUP = @"users/signup";
static NSString *const SOCIAL_SIGNIN = @"users/social_signin";
static NSString *const SEND_VERIFICATION = @"users/forgotpass";
static NSString *const VERIFY = @"users/verify";
static NSString* const UPDATEPASS = @"users/updatepass";
static NSString *const USER_UPDATE = @"users/update";

static NSString *const CREATE_NEW_ITEM = @"items/add_new_item";
static NSString *const UPDATE_ITEM = @"items/update_item";
static NSString *const GET_ITEM_LIST = @"items/get_items/";
static NSString *const GET_MY_ITEM_LIST = @"items/get_my_items/";
static NSString *const GET_ITEM_LIST_WITH_CHAT = @"chats/get_items_with_chat/";
static NSString *const GET_CHAT_LIST_WITH_ITEM = @"chats/get_chats_with_item/";
static NSString *const UPLOAD_FILE = @"files/upload/";
static NSString *const GET_ITEM = @"items/get_item/";
static NSString *const DEL_ITEM = @"items/del_item/";
static NSString *const GET_CHAT_MESSAGE_LIST = @"chats/get_messages/";

//UserDNSString *constNSString *constefaults
static NSString *const USER_TOKEN = @"token";
static NSString *const USER_ID = @"_id";
static NSString *const USER_Lat = @"user_lat";
static NSString *const USER_Lng = @"user_lng";
static NSString *const USER_OBJECT_STRING = @"user_obj";
static NSString *const IS_LOGED_IN = @"is_loged_in";
static NSString *const NOT_FIRST_TIME = @"not_first_time";
static NSString *const IS_SOCIAL = @"is_social";
static NSString* const RECENT_LOC = @"recent_loc";

static NSString* const LOCATION_SERVICE_LAT = @"service_location_lat";
static NSString* const LOCATION_SERVICE_LNG = @"service_location_lng";

static NSString* const LOCATION_LAT = @"location_lat";
static NSString* const LOCATION_LNG = @"location_lng";

static CLLocationManager * locationManager;
extern CLLocation *currentLocation;

static NSString *chatting_user_id_string = @"push_user_id";
static NSString *chatting_user_image_string = @"push_user_image";
static NSString *chatting_user_firstname_string = @"push_first_name";
static NSString *chatting_user_lastname_string = @"push_last_name";
static NSString *chatting_item_id_string = @"push_item_itemid";
static NSString *chatting_flag_string = @"push_flag";

static NSInteger     chatting_flag = 0;
static NSInteger chatting_userid = -1;
static NSInteger     chatting_itemid = -1;
static NSString *chatting_firstname = @"";
static NSString *chatting_lastname = @"";
static NSString *chatting_userimageurl=@"";

//static NSInteger    mEnteredChatting;


@interface AppUtils : NSObject
@property (nonatomic) WNAActivityIndicator *activityIndicator;

+ (SocketIOClient *) mSocket;
+ (void) setMSocket:(SocketIOClient *)value;
+ (SocketManager *) socketManager;
+ (void) setSocketManager:(SocketManager *)value;

+ (NSInteger) mEnteredChatting;
+ (void) setMEnteredChatting:(NSInteger)value;
+(void) setEnteredChatting:(BOOL)val;
+(BOOL) getEnteredChatting;
+(AppUtils *) sharedInstance;
+(void)removeActivityIndicator :(UIView *) parentView;
+(void)addActivityIndicator:(UIView *) parentView;
+ (UIViewController *)currentTopViewController;
+(void)showMessageWithOk:(NSString*)message withTitle:(NSString *)title;


+(BOOL)isTextEmpty :(NSString *) text;
+(BOOL)isValidEmail:(NSString *) text;
+(void)saveUserInfo : (id) responseObject;
+(void)saveUserAccountInfo:(NSDictionary *)dict;
+(NSString *)getUserToken;
+(BOOL)isLoggedIn;
+(BOOL)isNotFirstTime;
+(void)setNotFirstTime;
+(void)setLogOut;
+(NSDictionary *)getUserInfo;
+(NSInteger )getUserId;
+(NSString *)getUserImageUrl;
+(NSString *)getUserName;
+(UIColor*)colorWithHexString:(NSString*)hex;
+ (UIImage*)rotateUIImage:(UIImage*)sourceImage clockwise:(BOOL)clockwise;
+ (UIImage *) rotate: (UIImage *) image;

+(CLLocation *) getUserLocation;
+(void) saveUserLocation:(CLLocation *) loc;

+(CLLocation *)getServiceLocation;
+(void) setServiceLocation:(CLLocation *) loc;

+(CLLocation *)getLocationLocation;
+(void) setLocationLocation:(CLLocation *) loc;


+(NSString *)displayCurrencyInfoForLocale;
+(NSString *)getStringFromDictionary:(NSDictionary *)dict objectForKey:(NSString *)key;

+(NSInteger )isAppRunning;
+(void)setAppRunning:(NSInteger)para;

+(void)setNotifiUserData:(NSInteger)userId itemID:(NSInteger)nsItemId 
fName:(NSString *)first_name lName:(NSString *)last_name uImage:(NSString *)userImage;
+(ChatItemModel *)getNotifiUserData;

+(NSInteger)isChatPush;
+(void)setChatPush:(NSInteger) para;
@end

NS_ASSUME_NONNULL_END
