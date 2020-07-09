//
//  HomeViewCell.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "HomeViewCell.h"

@implementation HomeViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setModel:(MyItemModel *)model{
    _model = model;
    
    self.title.text = _model.itemName;
    NSString *price = [NSString stringWithFormat:@"%ld" ,_model.itemPrice];
    self.priceView.text = [price stringByAppendingString:_model.itemPriceUnit];
    self.distanceView.text = [NSString stringWithFormat:@"%.2fKm", _model.distance];

    
    [self.itemImageView sd_setImageWithURL:[NSURL URLWithString:_model.item_image_id]
                     placeholderImage:[UIImage imageNamed:@"ic_logo_cir_red"]];
    
    [self setNeedsLayout];
}
+ (CGFloat)heightForCellWithPost:(MyItemModel *)model {
    return (CGFloat)fmaxf(80.0f, 45.0f);
}

@end
