//
//  HomeViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-05.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#ifndef HomeViewController_h
#define HomeViewController_h

#import <UIKit/UIKit.h>
#import "../SubView/SubHomeView.h"
#import "../Utils/AppUtils.h"
#import "../SubView/SubHomeView.h"
@interface HomeViewController : UIViewController <CLLocationManagerDelegate>
- (IBAction)onBtnMenuClick:(id)sender;
@property (weak, nonatomic) IBOutlet UIView *mSubView;
@property (weak, nonatomic) IBOutlet UILabel *mLblTitle;
-(void)setTitle:(NSString *)title;
-(void)startUpdateDeviceToken:(NSString *)token;
@end

#endif /* HomeViewController_h */
