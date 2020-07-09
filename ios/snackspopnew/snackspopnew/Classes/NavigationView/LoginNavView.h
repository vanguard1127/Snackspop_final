//
//  LoginNavView.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../ItemViews/NavTableViewCell.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "../Utils/AppUtils.h"
NS_ASSUME_NONNULL_BEGIN
typedef void (^DidMenuClickedCallback)(NSInteger index, NSString* title);

@interface LoginNavView : UIView
@property (copy, nonatomic) DidMenuClickedCallback menuCallback;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UIImageView *logoImageView;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) NSString * userName;
@property (weak, nonatomic) NSString * userImage;
-(void) setSelectTableViewCell:(NavTableViewCell *)cell;

@end

NS_ASSUME_NONNULL_END
