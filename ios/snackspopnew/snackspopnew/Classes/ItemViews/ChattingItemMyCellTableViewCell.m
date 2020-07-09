//
//  ChattingItemMyCellTableViewCell.m
//  snackspopnew
//
//  Created by Admin on 12/04/2019.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "ChattingItemMyCellTableViewCell.h"

@implementation ChattingItemMyCellTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
    
    self.messageLabel.layer.cornerRadius = 8;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (void)layoutSubviews
{
    [super layoutSubviews];
//    [self.contentView layoutIfNeeded];

    
    

}
@end
