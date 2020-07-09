//
//  AddNewItemViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ImageCropperViewController.h"
#import "../Utils/AppUtils.h"
#import "../Models/MyItemModel.h"
#import <SDWebImage/UIButton+WebCache.h>
#import "MBAutoGrowingTextView.h"
#import "UITextView+Placeholder.h"


NS_ASSUME_NONNULL_BEGIN

typedef void (^disMissCallback)(NSInteger index);


@interface AddNewItemViewController : UIViewController
- (IBAction)onBtnBack:(id)sender;
- (IBAction)onBtnSave:(id)sender;
- (IBAction)onBtnUpload:(id)sender;
@property (weak, nonatomic) IBOutlet UIButton *itemImageView;
@property (weak, nonatomic) IBOutlet UILabel *titleView;

@property (weak, nonatomic) IBOutlet MBAutoGrowingTextView *itemDescription;

@property (weak, nonatomic) IBOutlet UITextField *itemPrice;
@property (weak, nonatomic) IBOutlet UITextField *itemName;
@property (weak, nonatomic) IBOutlet UILabel *itemPriceUnit;

@property (weak, nonatomic) MyItemModel * model;
@property (weak , nonatomic) UIImage *image;
@property (copy, nonatomic) disMissCallback mdismisCallBack;

@property (assign ,nonatomic) NSInteger isUpdate;
-(void)startAsyncCreate;
-(void)startAsyncUpload;

-(NSDictionary *)makeRequestUploadParams;
-(NSDictionary *)makeRequestParams;

@end

NS_ASSUME_NONNULL_END
