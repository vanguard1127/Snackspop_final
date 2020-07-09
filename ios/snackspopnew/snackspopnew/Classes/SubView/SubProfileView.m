//
//  SubProfileView.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "SubProfileView.h"

@implementation SubProfileView

NSString *type;
NSString *title;
NSString *text;
NSString *image_url;
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self setData];
    
}

-(void)setData{
    NSDictionary *userData = [AppUtils getUserInfo];
    NSString *first_name = [AppUtils getStringFromDictionary:userData
                                                objectForKey:@"first_name"];
    NSString *last_name = [AppUtils getStringFromDictionary:userData
                                               objectForKey:@"last_name"];
    NSString *email = [AppUtils getStringFromDictionary:userData
                                           objectForKey:@"email"];
    NSString *phoneNumber = [AppUtils getStringFromDictionary:userData
                                                 objectForKey:@"phone_number"];
    id imageIdID = [userData objectForKey:@"photo"];
    NSString *image_photo_url = @"";
    if ((NSNull *)imageIdID != [NSNull null])
    {
        NSString *image_url = [BASE_URL stringByAppendingString:IMAGE_URL];
        image_photo_url = [image_url stringByAppendingString:[NSString stringWithFormat:@"%@" ,imageIdID]];
        
    }
    [self.photoImageView sd_setBackgroundImageWithURL:[NSURL URLWithString:image_photo_url] forState:UIControlStateNormal placeholderImage:[UIImage imageNamed:@"ic_emptyuser"]];
    [self.lblFirstName setText:first_name];
    [self.lblLastName setText:last_name];
    [self.lblEmail setText:email];
    [self.lblPhoneNumber setText:phoneNumber];
    
    
    
    
}
-(void ) showDetailView{
    
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    ChangeDetailViewController *controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"ChangeDetailViewId"];
    
    controller.typeText = type;
    controller.mTitle = title;
    controller.text = text;
    
    controller.mCallBack= ^(NSInteger integerval)
    {
        [self setData];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"UserInfoUpdated" object:nil userInfo:nil];
    };
    [currentTopVC presentViewController:controller animated:true completion:nil];
}
- (IBAction)onBtnNameClick:(id)sender {
    
    type =@"first_name";
    title=@"Update First Name";
    text = self.lblFirstName.text;
    [self showDetailView];
}

- (IBAction)onBtnLastNameClick:(id)sender {
    type =@"last_name";
    title=@"Update Last Name";
    text = self.lblLastName.text;
    [self showDetailView];
}

- (IBAction)onBtnPhoneClick:(id)sender {
    type =@"phone_number";
    title=@"Update Phone Number";
    text = self.lblPhoneNumber.text;
    [self showDetailView];
}

- (IBAction)onBtnChangePwdClick:(id)sender {
    
    type =@"password";
    title=@"New Pssword";
    text = @"";
    [self showDetailView];
    
}

- (IBAction)onBtnImageClick:(id)sender {
    
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    ImageCropperViewController *controller =[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"ImageCropperViewId"];
    
    controller.didDismiss = ^(UIImage * _Nonnull data) {
        if (data != nil)
        {

	            [self startAsyncUpload:data];
            
            
            
            
        }
    };
    
    [currentTopVC presentViewController:controller animated:true completion:nil];
}

-(void)startAsyncUpload:(UIImage *)image
{
    [AppUtils addActivityIndicator:self];
    NSString *serverurl = [BASE_URL stringByAppendingString:UPLOAD_FILE];

    NSData *imageData = UIImageJPEGRepresentation(image,1);
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    
    [manager.requestSerializer setValue:@"multipart/form-data" forHTTPHeaderField:@"Content-Type"];
    
    [manager POST:serverurl parameters:nil constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
        [formData appendPartWithFileData:imageData
                                    name:@"imageupload"
                                fileName:@"item_image.jpg" mimeType:@"image/jpeg"];
        
        // etc.
    } progress:nil success:^(NSURLSessionDataTask *task, id responseObject) {
        
        NSLog(@"Upload Success!");
        NSLog(@"Response: %@", responseObject);
        
        NSString *str = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        NSLog(@"%@",str);
        NSError *jsonError;
        NSData *data = [str dataUsingEncoding:NSUTF8StringEncoding];
        NSDictionary * json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers  error:&jsonError];
        
        
        
        self.mUploadedImageId = [[json objectForKey:@"id"] integerValue];
        
        
        
        [AppUtils removeActivityIndicator:self];
        [self startAsyncUpdate];
        
        
        
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        NSLog(@"Error: %@", error);
        
        [AppUtils removeActivityIndicator:self];
        
        if (error.code == -1004)
        {
            [AppUtils showMessageWithOk:@"Server down or issue with the internet connection. Please check your connection." withTitle:@"Alert"];
        }
        else if (error.code == -1005)
        {
            [AppUtils showMessageWithOk:@"Server down or issue with the internet connection." withTitle:@"Alert"];
        } else {
            NSError *jsonError;
            NSData * data = error.userInfo[AFNetworkingOperationFailingURLResponseDataErrorKey];
            
            NSDictionary * json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers  error:&jsonError];
            [AppUtils showMessageWithOk:[json objectForKey:@"error"] withTitle:@"Alert"];
        }
    }];
    
    
}
-(void)startAsyncUpdate
{
    [AppUtils addActivityIndicator:self];
    NSString *serverurl = [BASE_URL stringByAppendingString:USER_UPDATE ];
    
    NSDictionary *params = [self makeRequestParams];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[AppUtils getUserToken] forHTTPHeaderField:@"x-access-token"];
    
    [manager PUT:serverurl
      parameters:params
         success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
             NSLog(@"Update Success!");
             NSLog(@"JSON: %@", responseObject);
             NSDictionary *user = [responseObject objectForKey:@"user"];
             [AppUtils saveUserAccountInfo:user];
             
             [self setData];

             
             [[NSNotificationCenter defaultCenter] postNotificationName:@"UserInfoUpdated" object:nil userInfo:nil];
             [AppUtils removeActivityIndicator:self];
             
         } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
             NSLog(@"Error: %@", error);
             
             [AppUtils removeActivityIndicator:self];
             
             if (error.code == -1004)
             {
                 [AppUtils showMessageWithOk:@"Server down or issue with the internet connection. Please check your connection." withTitle:@"Alert"];
             }
             else if (error.code == -1005)
             {
                 [AppUtils showMessageWithOk:@"Server down or issue with the internet connection." withTitle:@"Alert"];
             } else {
                 NSError *jsonError;
                 NSData * data = error.userInfo[AFNetworkingOperationFailingURLResponseDataErrorKey];
                 
                 NSDictionary * json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers  error:&jsonError];
                 [AppUtils showMessageWithOk:[json objectForKey:@"error"] withTitle:@"Alert"];
             }
             
         }];
    
}
-(NSDictionary *)makeRequestParams
{
    //NSNumber *number = [NSNumber numberWithInteger:self.mUploadedImageId];
    NSString *idString = [NSString stringWithFormat:@"%ld",self.mUploadedImageId ];
    
    NSDictionary *params = @{@"photo":idString};
    return params;
}
@end
