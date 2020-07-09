//
//  ChattingItemCell.m
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "ChattingItemCell.h"

@implementation ChattingItemCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (void)layoutSubviews
{
    [super layoutSubviews];
    [self.contentView layoutIfNeeded];
    //self.lineLabel.preferredMaxLayoutWidth = CGRectGetWidth(self.lineLabel.frame);
}
- (void)setModel:(ChattingItemModel *)model{
    _model = model;
    
    
    [self setNeedsLayout];
}
@end
