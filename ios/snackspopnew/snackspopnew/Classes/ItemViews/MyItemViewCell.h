//
//  MyItemViewCell.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Models/MyItemModel.h"
#import <SDWebImage/UIImageView+WebCache.h>

typedef void (^MyItemViewCellDeleteClickedCallBack)(MyItemModel *model);

NS_ASSUME_NONNULL_BEGIN

@interface MyItemViewCell : UITableViewCell

@property (copy, nonatomic) MyItemViewCellDeleteClickedCallBack menuCallback;
@property (weak, nonatomic) IBOutlet UIImageView *itemImageView;
@property (weak, nonatomic) IBOutlet UILabel *itemTitle;
@property (weak, nonatomic) IBOutlet UILabel *itemPrice;

@property (nonatomic , strong) MyItemModel *model;

- (IBAction)onBtnItemClick:(id)sender;


@end

NS_ASSUME_NONNULL_END
