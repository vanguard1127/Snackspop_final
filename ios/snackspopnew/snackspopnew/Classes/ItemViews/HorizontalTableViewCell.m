//
//  HorizontalTableViewCell.m
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "HorizontalTableViewCell.h"

@implementation HorizontalTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}


- (void)setModel:(MyItemModel *)model{
    _model = model;
    
    self.itemTitle.text = _model.itemName;
   [self.itemImageView sd_setImageWithURL:[NSURL URLWithString:_model.item_image_id]
                          placeholderImage:[UIImage imageNamed:@"ic_logo_cir_red"]];
    
    [self setNeedsLayout];
}
@end
