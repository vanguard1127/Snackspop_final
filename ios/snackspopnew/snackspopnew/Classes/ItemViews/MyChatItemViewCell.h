//
//  MyChatItemViewCell.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Models/ChatItemModel.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "../Utils/AppUtils.h"
NS_ASSUME_NONNULL_BEGIN

@interface MyChatItemViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *txtTitle;
@property (weak, nonatomic) IBOutlet UILabel *txtLastMsg;
@property (weak, nonatomic) IBOutlet UIImageView *imgView;
@property (weak, nonatomic) IBOutlet UILabel *txtUnread;


@property (nonatomic , strong) ChatItemModel *model;

@property (nonatomic , assign) NSInteger mFlag;
@end

NS_ASSUME_NONNULL_END
