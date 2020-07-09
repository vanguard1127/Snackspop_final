//
//  ForgotPasswordViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "ForgotPasswordViewController.h"

@interface ForgotPasswordViewController ()

@end

@implementation ForgotPasswordViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self.txtEmail becomeFirstResponder];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)btnSendClick:(id)sender {
    NSLog(@"BtnSendClick");
    
    NSString *email = self.txtEmail.text;
    NSString *errorText = @"Please Enter";
    NSString *usernameBlankText = @" a Email";
    NSString *emailValite = @"Please Input Valid Email!";
    BOOL textError = NO;
    if ([AppUtils isTextEmpty:email])
    {
        errorText = [errorText stringByAppendingString:usernameBlankText];
        textError = YES;
        [self.txtEmail becomeFirstResponder];
        
    }
    else if (![AppUtils isValidEmail:email]){
        textError = YES;
        errorText = emailValite;
    }
    else
    {
        [self startAsyncForogtPassword];
    }
}

- (IBAction)onBackBtnClick:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

-(void)startAsyncForogtPassword
{
    [AppUtils addActivityIndicator:self.view];
    NSString *serverurl = [BASE_URL stringByAppendingString:SEND_VERIFICATION ];
    
    NSDictionary *params = [self makeRequestParams];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager POST:serverurl parameters:params progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        NSLog(@"ForgotPassword Success!");
        NSLog(@"JSON: %@", responseObject);
        
        [AppUtils saveUserInfo:responseObject];
        
        [self dismissViewControllerAnimated:YES completion:NULL];
        [AppUtils removeActivityIndicator:self.view];
        
        
    } failure:^(NSURLSessionTask *operation, NSError *error) {
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
    NSDictionary *params = @{@"email":self.txtEmail.text};
    return params;
}
@end
