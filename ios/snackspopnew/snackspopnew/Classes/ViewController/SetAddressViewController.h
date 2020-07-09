//
//  SetAddressViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Utils/AppUtils.h"
#import <CoreLocation/CoreLocation.h>
NS_ASSUME_NONNULL_BEGIN

typedef void (^disMissCallback)(NSInteger index);
@interface SetAddressViewController : UIViewController

@property (copy, nonatomic) disMissCallback mdismisCallBack;
@property (weak, nonatomic) IBOutlet UITextField *mAddrLine1;
- (IBAction)onBtnSave:(id)sender;
- (IBAction)onBtnBack:(id)sender;
@property (weak, nonatomic) IBOutlet UITextField *mAddrLine2;
@property (weak, nonatomic) IBOutlet UITextField *mCountry;
@property (weak, nonatomic) IBOutlet UITextField *mState;
@property (weak, nonatomic) IBOutlet UITextField *mCity;
@property (weak, nonatomic) IBOutlet UITextField *mZipCode;

@property (strong , nonatomic) CLLocation *myLocation;
-(void)startAsyncGetMyLocation;
-(void)startUpdateAddress;
-(NSDictionary *)makeRequestParams;

@end

NS_ASSUME_NONNULL_END
