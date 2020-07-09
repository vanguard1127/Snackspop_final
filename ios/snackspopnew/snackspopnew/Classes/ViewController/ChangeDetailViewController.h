//
//  ChangeDetailViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Utils/AppUtils.h"


NS_ASSUME_NONNULL_BEGIN
typedef void (^disMissCallback)(NSInteger index);

@interface ChangeDetailViewController : UIViewController
- (IBAction)onBtnBackClick:(id)sender;
- (IBAction)onBtnChangeClick:(id)sender;
@property (weak, nonatomic) IBOutlet UITextField *txtName;
@property (weak, nonatomic) IBOutlet UILabel *lblTitle;
@property (weak, nonatomic) IBOutlet UIButton *btnUpdate;

@property( readwrite ,nonatomic, copy) NSString *mTitle;
@property( readwrite ,nonatomic, copy) NSString *text;
@property( readwrite ,nonatomic, copy) NSString *typeText;

@property (copy , nonatomic) disMissCallback mCallBack;
-(void)startAsyncUpdate;
-(NSDictionary *)makeRequestParams;
@end
;
NS_ASSUME_NONNULL_END
