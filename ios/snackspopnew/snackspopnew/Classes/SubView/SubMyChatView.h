//
//  SubMyChatView.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KafkaRefresh.h"
#import "../ItemViews/MyChatItemViewCell.h"
#import "../ViewController/ChatItemUserViewController.h"

#import "../Models/ChatItemModel.h"
#import "../../AppDelegate.h"
#import "../Utils/AppUtils.h"
NS_ASSUME_NONNULL_BEGIN

@interface SubMyChatView : UIView
@property (weak, nonatomic) IBOutlet UITableView *tableView;
-(void)reloadData;
-(NSDictionary *)makeRequestParams;
@end

NS_ASSUME_NONNULL_END
