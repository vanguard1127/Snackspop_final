//
//  SignInViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HomeViewController.h"
#import "ForgotPasswordViewController.h"
#import "CreateNewAccountViewController.h"
#import "FBSDKLoginKit.h"
#import "../Utils/AppUtils.h"
#import "../../AppDelegate.h"

@interface SignInViewController : UIViewController
- (IBAction)btnLoginClick:(id)sender;
- (IBAction)btnCreateNewAccountClick:(id)sender;
- (IBAction)btnForgotPasswordClick:(id)sender;
- (IBAction)btnFBClick:(id)sender;
- (IBAction)btnGoogleClick:(id)sender;
@property (weak, nonatomic) IBOutlet UITextField *txtUserName;
@property (weak, nonatomic) IBOutlet UITextField *txtPassword;

@property (weak, nonatomic) IBOutlet FBSDKLoginButton *mBtnFcb;

@property (strong ,nonatomic) NSString *firToken;
-(void)startAsyncLogin;
-(NSDictionary *)makeRequestParams;

-(void)startAsyncSocailLogin:(NSString
                              *)token email:(NSString *)eS firstName:(NSString *) fn LastName:(NSString *)ln;

-(void)startUpdateDeviceToke:(NSString *)token;

@end


