//
//  SubHomeView.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "SubHomeView.h"
#import "../../AppDelegate.h"
#import "KafkaRefresh.h"
@interface SubHomeView() <UITableViewDataSource, UITableViewDelegate>
@property (readwrite, nonatomic, strong) NSMutableArray *myItems;
@property (readwrite , nonatomic) BOOL isUpdatedLocation;

@end
@implementation SubHomeView
bool isSortPrice = false, isDistanceAccending = true, isPriceAccending = true;
bool isMyProduct = false;
NSInteger limit = 100;
NSInteger page = 1;

-(void)awakeFromNib{
    [super awakeFromNib];
    _isUpdatedLocation = NO;
    [self.txtSearch addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    [self.tableView bindGlobalStyleForFootRefreshHandler:^{
        page +=1 ;
        [self reloadData];
    }];
    [self.tableView bindGlobalStyleForHeadRefreshHandler:^{
        page = 1;
        
        [self reloadData];
    }];
    [self.tableView registerNib:[UINib nibWithNibName:@"raw_home_cell" bundle:nil]
         forCellReuseIdentifier:@"HomeViewCellId"];
    
    
    
    [[NSNotificationCenter defaultCenter]
     addObserver:self selector:@selector(updatedTriggerNotification:) name:@"UpdatedLocation" object:nil];
    
    CLLocation *myLocation = [AppUtils getServiceLocation];
    if ([AppUtils isLoggedIn])
    {
        CLLocation *userLocation = [AppUtils getUserLocation];
        if ((NSNull *)userLocation != [NSNull null] && userLocation.coordinate.latitude != 0 )
        {
            myLocation = userLocation;
        }
    }
    
    if (myLocation.coordinate.latitude == 0)
    {
        NSDictionary *dict = [NSDictionary dictionaryWithObject:@{@"type":@"1"} forKey:@"message"];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"NotificationMessageEvent" object:nil userInfo:dict];
    }
    else
        [self reloadData];
    
    
    [self setArrowVisible];
    
    [self.mLblNoGoodies setHidden:true];
    
    _myItems = [[NSMutableArray alloc] init];
    
    self.txtSearch.layer.masksToBounds = YES;
    self.txtSearch.layer.cornerRadius = 15;
    
    
    
}



-(void)setArrowVisible
{
    if (isSortPrice)
    {
        [self.imgPrice setHidden:false];
        [self.imgDistance setHidden:true];
        if (isPriceAccending)
        {
            self.imgPrice.image = [UIImage imageNamed:@"ic_arrow_down"];
        }
        else{
            self.imgPrice.image = [UIImage imageNamed:@"ic_arrow_up"];;
        }
    }
    else
    {
        [self.imgPrice setHidden:true];
        [self.imgDistance setHidden:false];
        if (isDistanceAccending)
        {
            self.imgDistance.image = [UIImage imageNamed:@"ic_arrow_down"];
        }
        else{
            self.imgDistance.image = [UIImage imageNamed:@"ic_arrow_up"];
        }
    }
}


-(void)reloadData
{
   
    NSString *serverurl = [BASE_URL stringByAppendingString:GET_ITEMS ];
    
    NSDictionary *params = [self makeRequestParams];
    
    [self.mLblNoGoodies setHidden:true];
    [AppUtils addActivityIndicator:self];
        

    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager POST:serverurl parameters:params progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        
        NSLog(@"JSON: %@", responseObject);
        NSDictionary *receivedData = responseObject;
        NSArray *dataList = [receivedData objectForKey:@"data"];
        if (page == 1)
        {
            [self.myItems removeAllObjects];
        }
        for (int i = 0 ; i < dataList.count; i++)
        {
            NSDictionary *itemObject = [dataList objectAtIndex:i];
            MyItemModel *model = [[MyItemModel alloc] init];
            model.itemName = [itemObject objectForKey:@"item_name"];
            model.itemId = [[itemObject objectForKey:@"id"] integerValue];
            model.itemDescription = [itemObject objectForKey:@"item_description"];
            model.itemPrice = [[itemObject objectForKey:@"item_price"] integerValue];
            model.itemPriceUnit =[itemObject objectForKey:@"item_unit"];
            model.userId = [[itemObject objectForKey:@"user_id"] integerValue];
            NSDictionary *userItemObject = [itemObject objectForKey:@"user"];
            model.distance = [[userItemObject objectForKey:@"distance"] doubleValue];
   
            NSNumber *user_photo = [userItemObject objectForKey:@"photo"];
            model.user_image_url = @"";
            if ((NSNull *)user_photo != [NSNull null])
            {
                NSString *image_url = [BASE_URL stringByAppendingString:IMAGE_URL];
                
                model.user_image_url = [image_url stringByAppendingString:[NSString stringWithFormat:@"%@" ,user_photo]];
            }
            
            NSArray *image = [itemObject objectForKey:@"images"];
            if (image.count > 0)
            {
                NSDictionary *itemImageOject = [image objectAtIndex:0];
                NSNumber *image_id = [itemImageOject objectForKey:@"image"];
                NSString *image_url = [BASE_URL stringByAppendingString:IMAGE_URL];
                
                model.item_image_id = [image_url stringByAppendingString:[NSString stringWithFormat:@"%@" ,image_id]];
            }
            
            [self.myItems addObject:model];
        }
        [AppUtils removeActivityIndicator:self];
        if ([self.myItems count] == 0)
        {
            if (page > 1)
            {
                page = page - 1;
            }
            [self.mLblNoGoodies setHidden:false];
        }
        [self.tableView reloadData];
        [self.tableView.headRefreshControl endRefreshing];
        [self.tableView.footRefreshControl endRefreshing];
        
    } failure:^(NSURLSessionTask *operation, NSError *error) {
        NSLog(@"Error: %@", error);
	
        [AppUtils removeActivityIndicator:self];
        
        if (error.code == -1004)
        {
            [AppUtils showMessageWithOk:@"Server down or issue with the internet connection. Please check your connection." withTitle:@"Alert"];
        }
        else if (error.code == -1005)
        {
            [AppUtils showMessageWithOk:@"Server down or issue with the internet connection." withTitle:@"Alert"];
        } else {
            NSError *jsonError;
            NSData * data = error.userInfo[AFNetworkingOperationFailingURLResponseDataErrorKey];
            
            NSDictionary * json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers  error:&jsonError];
            [AppUtils showMessageWithOk:[json objectForKey:@"error"] withTitle:@"Alert"];
        }
        [self.tableView.headRefreshControl endRefreshing];
        [self.tableView.footRefreshControl endRefreshing];
        
    }];
}

-(NSDictionary *)makeRequestParams
{
    NSString *keyword = [self.txtSearch text];
    NSNumber *sttype = isSortPrice ? [NSNumber numberWithInt:0] : [NSNumber numberWithInt:1];
    bool direction_type = false;
    direction_type = isSortPrice ? isPriceAccending : isDistanceAccending;
    NSNumber *direction =  direction_type ? [NSNumber numberWithInt:1] : [NSNumber numberWithInt: 0];
    NSNumber  *myProduct = isMyProduct ? [NSNumber numberWithInt:0] : [NSNumber numberWithInt:1];
    NSNumber  *me_user_id = [NSNumber numberWithInt:-1];
    
    
    CLLocation *myLocation = [AppUtils getServiceLocation];
    CLLocation *savedLocation = [AppUtils getLocationLocation];
    if ([AppUtils isLoggedIn])
    {
        me_user_id =[NSNumber numberWithInteger: [AppUtils getUserId]];
        CLLocation *myUserLocation = [AppUtils getUserLocation];
        
        if (myUserLocation.coordinate.longitude != 0 && myUserLocation.coordinate.latitude !=0)
        {
            myLocation = myUserLocation;
        }
    }
    else
    {
        if (savedLocation.coordinate.longitude != 0 && savedLocation.coordinate.latitude !=0 )
        {
            myLocation = savedLocation;
        }
    }
    NSNumber *latNum = [NSNumber numberWithFloat:myLocation.coordinate.latitude];
    NSNumber *lngNum = [NSNumber numberWithFloat:myLocation.coordinate.longitude];
    
    NSNumber *m_page = [NSNumber numberWithInteger:page];
    NSNumber *m_limit = [NSNumber numberWithInteger:limit];
    
    
    NSDictionary *params = @{@"keyword":keyword, @"sort_type":sttype, @"direction_type":direction ,@"page":m_page ,@"limit":m_limit ,@"user_id":myProduct, @"geo_lat" : latNum , @"geo_lng":lngNum ,@"me_user_id":me_user_id};
    return params;
}
- (void)textFieldDidChange:(UITextField *)textField {
    NSLog(@"text changed: %@", textField.text);
    
//    if (textField.text.length > 2 || textField.text.length ==0)
//    {
        [self reloadData];
//h    }
}
- (IBAction)onBtnDistanceClick:(id)sender {
    
    isSortPrice = false;
    isDistanceAccending = !isDistanceAccending;
    [self setArrowVisible];
    [self reloadData];
}

- (IBAction)onBtnPriceClick:(id)sender {
    isSortPrice = true;
    isPriceAccending = !isPriceAccending;
    [self setArrowVisible];
    [self reloadData];
}

- (IBAction)onBtnSetLocationClick:(id)sender {
    
    
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    SetLocationViewController *controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SetLocationViewControllerId"];
    controller.mdismisCallBack = ^(NSInteger para)
    {
        if (para == 1)
        {
            page = 1;
            [self reloadData];
        }

    };
    [currentTopVC presentViewController:controller animated:true completion:nil];
}

- (IBAction)onBtnAddNewItemClick:(id)sender {
    if (![AppUtils isLoggedIn])
    {

        
        dispatch_async(dispatch_get_main_queue(), ^{
            UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"Alert" message:@"First Login!" preferredStyle:UIAlertControllerStyleAlert];
            [alertController addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                
                UIViewController *currentTopVC = [AppUtils currentTopViewController];
                [currentTopVC presentViewController:[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SignInViewId"] animated:true completion:nil];
            }]];
            
            [[AppUtils currentTopViewController] presentViewController:alertController animated:YES completion:^{
            }];
        });
        
        return;
    }
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    [currentTopVC presentViewController:[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"AddNewItemViewId"] animated:true completion:nil];
    
}
- (NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_myItems count];
}

- (UITableViewCell*) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    static NSString *simpleTableIdentifier = @"HomeViewCell";
    
    HomeViewCell *cell = (HomeViewCell *)[tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    if (cell == nil)
    {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"raw_home_cell" owner:self options:nil];
        cell = [nib objectAtIndex:0];
        
        
    }
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    cell.model = [self.myItems objectAtIndex:indexPath.row];
    return cell;
    
}

#pragma mark - UITableViewDelegate
- (void) tableView:(UITableView *)tableView didHighlightRowAtIndexPath:(NSIndexPath *)indexPath
{
    
}

- (void) tableView:(UITableView *)tableView didUnhighlightRowAtIndexPath:(NSIndexPath *)indexPath
{

    
}
- (CGFloat)tableView:(__unused UITableView *)tableView
heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [HomeViewCell heightForCellWithPost:[self.myItems objectAtIndex:(NSUInteger)indexPath.row]];
}
- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OthersItemDetailViewController *otherController =[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"OthersItemDetailViewId"];
    MyItemModel *model = [self.myItems objectAtIndex:(NSUInteger)indexPath.row];
    otherController.model = model;
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    [currentTopVC presentViewController:otherController animated:true completion:nil];
}

#pragma mark - Notification
-(void) updatedTriggerNotification:(NSNotification *) notification
{
    CLLocation *serviceLocation = [AppUtils getServiceLocation];
    
    if (serviceLocation != nil && serviceLocation.coordinate.latitude != 0 && _isUpdatedLocation == NO)
    {
        _isUpdatedLocation = YES;
        [self reloadData];
    }
    
}
@end
