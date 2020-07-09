//
//  SubMyProductView.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../ItemViews/MyItemViewCell.h"
#import "../../AppDelegate.h"
#import "../Utils/AppUtils.h"
#import "../ViewController/MyItemDetailViewController.h"
#import "KafkaRefresh.h"
#import "../ViewController/SetLocationViewController.h"
#import "../ViewController/AddNewItemViewController.h"
NS_ASSUME_NONNULL_BEGIN

@interface SubMyProductView : UIView
@property (weak, nonatomic) IBOutlet UILabel *mLblNoGoodies;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
- (IBAction)onBtnSetLocationClick:(id)sender;
- (IBAction)onBtnAddNewItemClick:(id)sender;
@property (weak, nonatomic) IBOutlet UITextField *txtSearch;

-(void)reloadData;
-(NSDictionary *)makeRequestParams;
-(void)textFieldDidChange:(UITextField *)textField;

@end

NS_ASSUME_NONNULL_END
