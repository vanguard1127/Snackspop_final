//
//  ForgotPasswordViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Utils/AppUtils.h"
NS_ASSUME_NONNULL_BEGIN

@interface ForgotPasswordViewController : UIViewController
- (IBAction)btnSendClick:(id)sender;
- (IBAction)onBackBtnClick:(id)sender;
@property (weak, nonatomic) IBOutlet UITextField *txtEmail;

-(void)startAsyncForogtPassword;
-(NSDictionary *)makeRequestParams;
@end



NS_ASSUME_NONNULL_END
