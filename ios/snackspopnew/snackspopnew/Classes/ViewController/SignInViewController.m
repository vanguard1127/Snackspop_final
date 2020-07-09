//
//  SignInViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "SignInViewController.h"
#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import <FBSDKLoginKit/FBSDKLoginKit.h>
@import Firebase;

@interface SignInViewController () <FBSDKLoginButtonDelegate ,GIDSignInUIDelegate , GIDSignInDelegate>

@end

@implementation SignInViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    // Extend the code sample from 6a. Add Facebook Login to Your Code
    // Add to your viewDidLoad method:
//    self.mBtnFcb.readPermissions = @[@"public_profile", @"email"];
//    self.mBtnFcb.delegate = self;
    
//    if ([FBSDKAccessToken currentAccessToken]) {
//        // User is logged in, do work such as go to next view controller.
//        NSLog(@"%@" ,@"FaceBook Logged In");
//    }
    [[FIRInstanceID instanceID] instanceIDWithHandler:^(FIRInstanceIDResult * _Nullable result,
                                                        NSError * _Nullable error) {
        if (error != nil) {
            NSLog(@"Error fetching remote instance ID: %@", error);
        } else {
            NSLog(@"Remote instance ID token: %@", result.token);
            self.firToken = result.token;
        }
    }];
    
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)btnLoginClick:(id)sender {
    NSLog(@"BtnSignInClick");
    
    NSString *email = self.txtUserName.text;
    NSString *password = self.txtPassword.text;
    
    NSString *errorText = @"Please Enter";
    NSString *usernameBlankText = @" a Email";
    NSString *passwordBlankText = @" a password";
    NSString *emailValite = @"Please Input Valid Email!";
    BOOL textError = NO;
    
    if ([AppUtils isTextEmpty:email])
    {
        errorText = [errorText stringByAppendingString:usernameBlankText];
        textError = YES;
        [self.txtUserName becomeFirstResponder];
        
    }
    else if (![AppUtils isValidEmail:email]){
        textError = YES;
        errorText = emailValite;
    }
    else if ([AppUtils isTextEmpty:password])
    {
        textError = YES;
        errorText = [errorText stringByAppendingString:passwordBlankText];
        [self.txtPassword becomeFirstResponder];
    }
    else
    {
        [self startAsyncLogin];
    }
    if (textError) {
        [AppUtils showMessageWithOk:errorText withTitle:@"Alert"];
        return;
    }
    
    
}

-(void)startAsyncLogin
{
    [AppUtils addActivityIndicator:self.view];
    NSString *serverurl = [BASE_URL stringByAppendingString:LOGIN ];
    
    NSDictionary *params = [self makeRequestParams];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager POST:serverurl parameters:params progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        NSLog(@"Login Success!");
        NSLog(@"JSON: %@", responseObject);
        [AppUtils saveUserInfo:responseObject];
        
        [AppUtils removeActivityIndicator:self.view];
        
        HomeViewController *homeView = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"HomeViewId"];
        [self presentViewController:homeView animated:YES completion:nil];

        
    } failure:^(NSURLSessionTask *operation, NSError *error) {
        NSLog(@"Error: %@", error);

        [AppUtils removeActivityIndicator:self.view];
        if (error.code == -1004) {
            [AppUtils showMessageWithOk:@"Server down or issue with the internet connection. Please check your connection." withTitle:@"Alert"];
        } else if (error.code == -1005) {
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
    
    //TODO user login
    NSDictionary *params = @{@"email":self.txtUserName.text , @"password":self.txtPassword.text};
    return params;
}

-(void)startAsyncSocailLogin:(NSString*)token email:(NSString *)eS  firstName:(NSString *) fn  LastName:(NSString *)ln
{
    [AppUtils addActivityIndicator:self.view];
    NSString *serverurl = [BASE_URL stringByAppendingString:SOCIAL_SIGNIN ];
    


    NSNumber *number = [NSNumber numberWithInteger:1];
    NSDictionary *params = @{@"email":eS , @"social_token":token ,@"first_name":fn, @"last_name":ln ,@"device_type":number , @"device_id":self.firToken};
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager POST:serverurl parameters:params progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        NSLog(@"Login Success!");
        NSLog(@"JSON: %@", responseObject);
        [AppUtils saveUserInfo:responseObject];
        
        [AppUtils removeActivityIndicator:self.view];
        
        HomeViewController *homeView = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"HomeViewId"];
        [self presentViewController:homeView animated:YES completion:nil];
        
        
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


- (IBAction)btnCreateNewAccountClick:(id)sender {
    NSLog(@"BtnCreateNewAccountClick");
    CreateNewAccountViewController *view = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SignUpViewId"];
    [self presentViewController:view animated:YES completion:nil];
    
    
}

- (IBAction)btnForgotPasswordClick:(id)sender {
    NSLog(@"BtnForgotPasswordClick");
    ForgotPasswordViewController *forgotView = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"ForgotPasswordViewId"];
    [self presentViewController:forgotView animated:YES completion:nil];
    
}

- (IBAction)btnFBClick:(id)sender {
    NSLog(@"BtnFBClick");
    
    NSLog(@"%@" , FBSDKAccessToken.currentAccessToken.tokenString);
    FBSDKLoginManager *fBSDKLoginManager = [[FBSDKLoginManager alloc]init];
    
    if ([UIApplication.sharedApplication canOpenURL:[NSURL URLWithString:@"fb://"]])
    {
        fBSDKLoginManager.loginBehavior = FBSDKLoginBehaviorSystemAccount;
    }

    
    [fBSDKLoginManager logInWithReadPermissions:@[@"email" ,@"public_profile"] fromViewController:self handler:^(FBSDKLoginManagerLoginResult *result, NSError *error) {
        
        if (error) {
            
            NSLog(@"%@",error);
            
        }
        
        else if (result.isCancelled) {
            
            NSLog(@"result isCancelled");
            
        }
        
        else {
            
//            NSString *stringToken1 = result.token.tokenString;
            
            [FBSDKAccessToken setCurrentAccessToken:result.token];
            if ([result.grantedPermissions containsObject:@"email"]) {
                [[[FBSDKGraphRequest alloc]initWithGraphPath:@"me" parameters:@{@"fields":@"id, name, first_name, last_name, gender, picture.type(large), email"}]
                 startWithCompletionHandler:^(FBSDKGraphRequestConnection *connection, id result, NSError *error) {
                     
                     if (!error) {
                         
                         
                         //FBSDKAccessToken setCurrentAccessToken:result
                         NSString *stringToken = [result objectForKey:@"id"];
                         
                         [self startAsyncSocailLogin:stringToken
                                               email:[result valueForKey:@"email"] firstName:                   [result valueForKey:@"first_name"] LastName:[result valueForKey:@"last_name"]];
                         
                     }
                     
                     else {
                         
                         NSLog(@"%@",[error localizedDescription]);
                         
                         NSLog(@"%@",error);
                         
                     }
                 }
                 ];

            }
            
        }
    }
     ];
}

- (IBAction)btnGoogleClick:(id)sender {
    NSLog(@"BtnGoogleClick");
    
    [GIDSignIn sharedInstance].delegate = self;
    [GIDSignIn sharedInstance].uiDelegate = self;
    [[GIDSignIn sharedInstance] signIn];
}
- (void)loginButton:(FBSDKLoginButton *)loginButton didCompleteWithResult:(FBSDKLoginManagerLoginResult *)result error:(NSError *)error {
    NSLog(@"%@" ,@"FaceBook Logged In");
    NSLog(@"%@" ,result);
}

- (void)loginButtonDidLogOut:(FBSDKLoginButton *)loginButton {
    NSLog(@"%@" ,@"FaceBook Logged Out");
}



- (void)signIn:(GIDSignIn *)signIn presentViewController:(UIViewController *)viewController
{
    [self presentViewController:viewController animated:YES completion:nil];
}

- (void)signInWillDispatch:(GIDSignIn *)signIn error:(NSError *)error {
    
}
- (void)signIn:(GIDSignIn *)signIn didSignInForUser:(GIDGoogleUser *)user
     withError:(NSError *)error {
    //user signed in
    //get user data in "user" (GIDGoogleUser object)
    
    NSLog(@"%@" ,user);
    NSString *idToken = user.userID;
    NSString *givenName = user.profile.givenName;
    NSString *familyName = user.profile.familyName;
    NSString *email = user.profile.email;

    [self startAsyncSocailLogin:idToken
                          email:email
                      firstName:givenName
                       LastName:familyName];
    
}
-(void)startUpdateDeviceToke:(NSString *)token
{
}
@end

