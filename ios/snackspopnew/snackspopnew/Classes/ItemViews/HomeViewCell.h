//
//  HomeViewCell.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Models/MyItemModel.h"
#import <SDWebImage/UIImageView+WebCache.h>
NS_ASSUME_NONNULL_BEGIN

@interface HomeViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *title;
@property (weak, nonatomic) IBOutlet UILabel *priceView;
@property (weak, nonatomic) IBOutlet UILabel *distanceView;
@property (weak, nonatomic) IBOutlet UIImageView *itemImageView;
@property (nonatomic , strong) MyItemModel *model;
+ (CGFloat)heightForCellWithPost:(MyItemModel *)post;
@end

NS_ASSUME_NONNULL_END
