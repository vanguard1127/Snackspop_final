//
//  MyChatItemViewCell.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "MyChatItemViewCell.h"

@implementation MyChatItemViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setModel:(ChatItemModel *)model{
    _model = model;
    NSString *msg = _model.lastMessage;
    if (_model.lastMessage.length > 20)
    {
        msg = [[_model.lastMessage substringToIndex:20] stringByAppendingString:@"..."];
        
    }
    self.txtLastMsg.text =msg;
    
    if (_mFlag == 0)
    {
        self.txtTitle.text = _model.itemName;
      
        [self.imgView sd_setImageWithURL:[NSURL URLWithString:_model.item_image_id]
                        placeholderImage:[UIImage imageNamed:@"ic_logo_cir_red"]];
        NSInteger myUserId = [AppUtils getUserId];
        
        if (myUserId == _model.item_user_id)
        {
            if (_model.unreadCount == 0)
            {
                [self.txtUnread setHidden:true];
            }
            else{
                [self.txtUnread setHidden:false];
                NSString *string = [NSString stringWithFormat:@"%ld" ,_model.unreadCount];
                self.txtUnread.text = string;
                
                self.txtUnread.layer.masksToBounds = YES;
                self.txtUnread.layer.cornerRadius = 16;
            }
            self.backgroundColor = [UIColor whiteColor];
            
            
        }
        else
        {
            [self.txtUnread setHidden:true];
            [self setBackgroundColor:[AppUtils colorWithHexString:@"0xFCD8D8"]];
            
            
        }
    }
    else if (_mFlag == 1)
    {
        self.txtTitle.text = [[_model.user_first_name stringByAppendingString:@" "] stringByAppendingString:_model.user_last_name];
        
        [self.imgView sd_setImageWithURL:[NSURL URLWithString:_model.user_photo_id]
                        placeholderImage:[UIImage imageNamed:@"ic_emptyuser"]];
        
        if (_model.unreadCount == 0)
        {
            [self.txtUnread setHidden:true];
        }
        else{
            [self.txtUnread setHidden:false];
            NSString *string = [NSString stringWithFormat:@"%ld" ,_model.unreadCount];
            self.txtUnread.text = string;
            self.txtUnread.layer.masksToBounds = YES;
            self.txtUnread.layer.cornerRadius = 16;
        }
        
    }
    
    

    [self setNeedsLayout];
}

@end
