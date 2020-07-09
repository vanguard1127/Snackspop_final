//
//  NormalNavView.h
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../ItemViews/NavTableViewCell.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "LoginNavView.h"
NS_ASSUME_NONNULL_BEGIN

@interface NormalNavView : UIView
{
@private NSArray *tableData;
@private NSArray *thumbnails;
}
@property (copy, nonatomic) DidMenuClickedCallback menuCallback;
@property (weak, nonatomic) IBOutlet UILabel *mLblName;
@property (weak, nonatomic) IBOutlet UIImageView *logoImageView;

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (weak, nonatomic) NSString * userName;
@property (weak, nonatomic) NSString * userImage;

@end

NS_ASSUME_NONNULL_END
