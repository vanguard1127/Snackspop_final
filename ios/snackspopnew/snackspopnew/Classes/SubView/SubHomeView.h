//
//  SubHomeView.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../ViewController/SetLocationViewController.h"
#import "../ViewController/OthersItemDetailViewController.h"
#import "../ViewController/HomeViewController.h"
#import "../ItemViews/HomeViewCell.h"
#import "../Utils/AppUtils.h"
#import "../Networking/AFAppAPIClient.h"
#import "../Models/MyItemModel.h"
#import <CoreLocation/CoreLocation.h>
#import "../ViewController/HomeViewController.h"
NS_ASSUME_NONNULL_BEGIN

@interface SubHomeView : UIView
- (IBAction)onBtnDistanceClick:(id)sender;
- (IBAction)onBtnPriceClick:(id)sender;
- (IBAction)onBtnSetLocationClick:(id)sender;
- (IBAction)onBtnAddNewItemClick:(id)sender;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *mLblNoGoodies;
@property (weak, nonatomic) IBOutlet UIImageView *imgDistance;

@property (weak, nonatomic) IBOutlet UIImageView *imgPrice;
@property (weak, nonatomic) IBOutlet UITextField *txtSearch;
@property (strong, nonatomic) WNAActivityIndicator *activityIndicator;

@property (nonatomic) UIViewController *parentController;

-(void)reloadData;
-(void)setArrowVisible;
-(NSDictionary *)makeRequestParams;
-(void)textFieldDidChange:(UITextField *)textField;
@end

NS_ASSUME_NONNULL_END
