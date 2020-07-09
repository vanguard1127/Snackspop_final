//
//  ChattingItemCell.h
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Models/ChattingItemModel.h"
#import <SDWebImage/UIImageView+WebCache.h>
NS_ASSUME_NONNULL_BEGIN

@interface ChattingItemCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *leftImageUrl;
@property (weak, nonatomic) IBOutlet UILabel *txtMessage;
@property (weak, nonatomic) IBOutlet UILabel *txtTime;

@property (nonatomic , strong) ChattingItemModel *model;
@end

NS_ASSUME_NONNULL_END
