//
//  ChangeDetailViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "ChangeDetailViewController.h"

@interface ChangeDetailViewController ()

@end

@implementation ChangeDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self.lblTitle setText:_mTitle];
    [self.txtName setText:_text];
    if ([_typeText isEqualToString:@"password"])
    {
        [self.btnUpdate setTitle:@"Update Password" forState:UIControlStateNormal];
    }
    
    
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)onBtnBackClick:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

- (IBAction)onBtnChangeClick:(id)sender {
    
    [self startAsyncUpdate];
}
-(void)startAsyncUpdate
{
    [AppUtils addActivityIndicator:self.view];
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
//             dispatch_async(dispatch_get_main_queue(), ^{
                 // Update the UI on the main thread.
                 [self dismissViewControllerAnimated:YES completion: ^{
                     if (self.mCallBack != nil)
                     {
                         self.mCallBack(1);
                     }
                 }];
//             });
             
             [AppUtils removeActivityIndicator:self.view];
             
         } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
             NSLog(@"Error: %@", error);
             
             [AppUtils removeActivityIndicator:self.view];
             
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
    NSDictionary *params = @{_typeText:self.txtName.text};
    return params;
}

@end
