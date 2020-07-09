//
//  ChattingViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../ItemViews/ChattingItemCell.h"
#import "../ItemViews/ChattingItemMyCellTableViewCell.h"
#import "../Models/ChatItemModel.h"
#import "../Utils/AppUtils.h"
#import "MBAutoGrowingTextView.h"
#import "UITextView+Placeholder.h"
#import "../Utils/AppUtils.h"
@import IQKeyboardManager;

@import SocketIO;
NS_ASSUME_NONNULL_BEGIN

@interface ChattingViewController : UIViewController
- (IBAction)onBtnBack:(id)sender;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
- (IBAction)onBtnSend:(id)sender;


@property (weak, nonatomic) IBOutlet MBAutoGrowingTextView *mTextView;

@property (weak, nonatomic) IBOutlet UILabel *lblTitle;

@property (strong, nonatomic) ChatItemModel * model;

@property (copy, nonatomic) disMissCallback mdismisCallBack;
@property (weak, nonatomic) IBOutlet UIView *mTopView;

@property (nonatomic, strong) NSLayoutConstraint *bottomConstraint;
@property (nonatomic, strong) NSLayoutConstraint *topConstraint;


@property (nonatomic) NSInteger isKeyboardShown;
-(void)startAsyncGetMessages;
-(NSDictionary *)makeRequestParams;
-(void)refreshViewController:(ChatItemModel * )newModel;


@end

NS_ASSUME_NONNULL_END
