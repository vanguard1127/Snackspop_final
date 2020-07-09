//
//  ChatItemUserViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-07.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../ItemViews/MyChatItemViewCell.h"
#import "ChattingViewController.h"
#import "../Utils/AppUtils.h"
#import "../Models/ChatItemModel.h"

#import "KafkaRefresh.h"
NS_ASSUME_NONNULL_BEGIN

@interface ChatItemUserViewController : UIViewController
@property (weak, nonatomic) IBOutlet UITableView *tableView;
- (IBAction)onBtnBackClick:(id)sender;
@property (weak, nonatomic) ChatItemModel * model;
@property (weak, nonatomic) IBOutlet UILabel *lblTitle;
@property (copy, nonatomic) disMissCallback mdismisCallBack;
-(void)startAsyncGetChatUserItem;
-(NSDictionary *)makeRequestParams;

@end



NS_ASSUME_NONNULL_END
