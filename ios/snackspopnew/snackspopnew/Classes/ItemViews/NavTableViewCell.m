//
//  NavTableViewCell.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "NavTableViewCell.h"

@implementation NavTableViewCell

@synthesize mImageView = _mImageView;
@synthesize mLabelView =_mLabelView;

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
