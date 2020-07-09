//
//  ChattingItemMyCellTableViewCell.h
//  snackspopnew
//
//  Created by Admin on 12/04/2019.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
NS_ASSUME_NONNULL_BEGIN

@interface ChattingItemMyCellTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *messageLabel;
@property (weak, nonatomic) IBOutlet UILabel *dateView;
@property (weak, nonatomic) IBOutlet UIImageView *photoView;

@end

NS_ASSUME_NONNULL_END
