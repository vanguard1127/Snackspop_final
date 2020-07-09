//
//  OthersItemDetailViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ChattingViewController.h"
#import "../Models/MyItemModel.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "../Utils/AppUtils.h"
NS_ASSUME_NONNULL_BEGIN

@interface OthersItemDetailViewController : UIViewController
- (IBAction)onBtnCallClick:(id)sender;
- (IBAction)onBtnMessageClick:(id)sender;
- (IBAction)onBtnBackClick:(id)sender;
@property (weak, nonatomic) IBOutlet UIImageView *itemImgView;
@property (weak, nonatomic) IBOutlet UILabel *itemTitleView;
@property (weak, nonatomic) IBOutlet UILabel *itemPriceView;
@property (weak, nonatomic) IBOutlet UILabel *itemDescriptionView;
@property (weak, nonatomic) IBOutlet UILabel *itemDistanceView;
@property (weak, nonatomic) IBOutlet UILabel *itemUsernameView;

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;

@property (weak, nonatomic) IBOutlet UILabel *otherIItemTile;

@property (weak, nonatomic) MyItemModel * model;

@property (weak, nonatomic) IBOutlet UIButton *test1;
@property (weak, nonatomic) IBOutlet UIButton *mBtnCall;
@property (weak, nonatomic) IBOutlet UIButton *mBtnMessage;

-(void)startAsyncGetItemInfo;
-(NSDictionary *)makeRequestParams;

@end

NS_ASSUME_NONNULL_END
