//
//  ImageCropperViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Utils/AppUtils.h"
NS_ASSUME_NONNULL_BEGIN

@interface ImageCropperViewController : UIViewController

@property (nonatomic, copy) void (^didDismiss)(UIImage *data);

- (IBAction)onBtnCameraClick:(id)sender;
- (IBAction)onBtnGalleryClick:(id)sender;
- (IBAction)onBtnSaveClick:(id)sender;
- (IBAction)onBtnCancel:(id)sender;
- (IBAction)onBtnSnapClick:(id)sender;
- (IBAction)onBtnRotateClick:(id)sender;
@property (weak, nonatomic) IBOutlet UIImageView *imageView;

@property (weak , nonatomic) UIImage *image;
@property (weak, nonatomic) IBOutlet UIButton *mBtnSnap;
@property (weak, nonatomic) IBOutlet UIButton *mBtnRotate;

@end

NS_ASSUME_NONNULL_END
