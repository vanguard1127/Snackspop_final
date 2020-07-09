//
//  MyItemViewCell.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "MyItemViewCell.h"

@implementation MyItemViewCell

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
    
    self.itemTitle.text = _model.itemName;
    NSString *price = [NSString stringWithFormat:@"%ld" ,_model.itemPrice];
    self.itemPrice.text = [price stringByAppendingString:_model.itemPriceUnit];
    
    [self.itemImageView sd_setImageWithURL:[NSURL URLWithString:_model.item_image_id]
                          placeholderImage:[UIImage imageNamed:@"ic_logo_cir_red"]];
    
    [self setNeedsLayout];
}
- (IBAction)onBtnItemClick:(id)sender {
    if (self.menuCallback != nil)
    {
        self.menuCallback(self.model);
    }
}
@end
