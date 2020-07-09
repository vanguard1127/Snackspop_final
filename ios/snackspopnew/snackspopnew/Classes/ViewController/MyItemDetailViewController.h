//
//  MyItemDetailViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-07.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AddNewItemViewController.h"
#import "../Models/MyItemModel.h"
#import "../Utils/AppUtils.h"
#import <SDWebImage/UIImageView+WebCache.h>

NS_ASSUME_NONNULL_BEGIN

@interface MyItemDetailViewController : UIViewController
- (IBAction)onBtnEditClick:(id)sender;
- (IBAction)onBtnBackClick:(id)sender;
@property (weak, nonatomic) IBOutlet UILabel *titleView;
@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UILabel *nameView;
@property (weak, nonatomic) IBOutlet UILabel *descriptionView;
@property (weak, nonatomic) IBOutlet UILabel *priceView;

@property (copy, nonatomic) disMissCallback mdismisCallBack;

@property (weak, nonatomic) MyItemModel * model;
@end

NS_ASSUME_NONNULL_END
