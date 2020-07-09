//
//  CreateNewAccountViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Utils/AppUtils.h"
#import "HomeViewController.h"
#import "TermsViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface CreateNewAccountViewController : UIViewController
- (IBAction)onBtnBack:(id)sender;
- (IBAction)onBtnCreateAccountClick:(id)sender;
- (IBAction)onBtnTermsServiceClick:(id)sender;
@property (weak, nonatomic) IBOutlet UITextField *txtFirstName;
@property (weak, nonatomic) IBOutlet UITextField *txtLastName;
@property (weak, nonatomic) IBOutlet UITextField *txtEmail;
@property (weak, nonatomic) IBOutlet UITextField *txtPhoneNum;
@property (weak, nonatomic) IBOutlet UITextField *txtPassword;


-(void)startAsyncSingUp;
-(NSDictionary *)makeRequestParams;
@end

NS_ASSUME_NONNULL_END
