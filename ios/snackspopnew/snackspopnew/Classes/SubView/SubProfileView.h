//
//  SubProfileView.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../../AppDelegate.h"
#import "../Utils/AppUtils.h"
#import "../ViewController/HomeViewController.h"
#import "../ViewController/ChangeDetailViewController.h"
#import "../ViewController/ImageCropperViewController.h"
#import <SDWebImage/UIButton+WebCache.h>

NS_ASSUME_NONNULL_BEGIN

@interface SubProfileView : UIView
- (IBAction)onBtnNameClick:(id)sender;
- (IBAction)onBtnLastNameClick:(id)sender;

- (IBAction)onBtnPhoneClick:(id)sender;
- (IBAction)onBtnChangePwdClick:(id)sender;
- (IBAction)onBtnImageClick:(id)sender;

@property (weak, nonatomic) IBOutlet UIButton *photoImageView;
@property (weak, nonatomic) IBOutlet UILabel *lblFirstName;
@property (weak, nonatomic) IBOutlet UILabel *lblLastName;
@property (weak, nonatomic) IBOutlet UILabel *lblEmail;
@property (weak, nonatomic) IBOutlet UILabel *lblPhoneNumber;
@property (nonatomic,assign) NSInteger mUploadedImageId;


@end

NS_ASSUME_NONNULL_END
