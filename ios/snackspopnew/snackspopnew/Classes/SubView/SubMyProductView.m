//
//  SubMyProductView.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "SubMyProductView.h"

@interface SubMyProductView() <UITableViewDataSource, UITableViewDelegate>
@property (readwrite, nonatomic, strong) NSMutableArray *myItems;
@end
@implementation SubMyProductView
NSInteger mlimit = 100;
NSInteger mpage = 1;
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

-(void)awakeFromNib{
    [super awakeFromNib];
    [self.txtSearch addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    
    
    [self.tableView bindGlobalStyleForFootRefreshHandler:^{
        NSLog(@"footer");
        mpage +=1 ;
        [self reloadData];
    }];
    [self.tableView bindGlobalStyleForHeadRefreshHandler:^{
        NSLog(@"header");
        mpage = 1;
        
        [self reloadData];
    }];
    
    [self.tableView registerNib:[UINib nibWithNibName:@"raw_my_item_cell" bundle:nil]
         forCellReuseIdentifier:@"MyItemViewCellId"];
    
    [self reloadData];
    
    
    [self.mLblNoGoodies setHidden:true];
    
    _myItems = [[NSMutableArray alloc] init];
    
    self.txtSearch.layer.masksToBounds = YES;
    self.txtSearch.layer.cornerRadius = 15;
    
}

- (NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.myItems count];
}

- (UITableViewCell*) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    static NSString *simpleTableIdentifier = @"MyItemViewCellId";
    
    MyItemViewCell *cell = (MyItemViewCell *)[tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    if (cell == nil)
    {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"raw_my_item_cell" owner:self options:nil];
        cell = [nib objectAtIndex:0];
    }
    
    cell.model = [self.myItems objectAtIndex:indexPath.row];
    
    cell.menuCallback = ^(MyItemModel *model)
    {
        UIViewController *currentTopVC = [AppUtils currentTopViewController];
        UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"Alert" message:@"Do you want to remove?" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction* yes = [UIAlertAction actionWithTitle:@"Yes" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
             [self startAsyncDeleteItem:model.itemId];
        }];
        UIAlertAction* no = [UIAlertAction
                                 actionWithTitle:@"No"
                                 style:UIAlertActionStyleDefault
                                 handler:^(UIAlertAction * action)
                                 {
                                     [alert dismissViewControllerAnimated:YES completion:nil];
                                     
                                 }];
        [alert addAction:yes];
        [alert addAction:no];
        [currentTopVC presentViewController:alert animated:YES completion:nil];
        
        

       
    };
    return cell;
    
}

-(void) startAsyncDeleteItem :(NSInteger )modelID
{
    NSNumber *idNumber =[NSNumber numberWithInteger:modelID];
    
    NSString *idString = [NSString stringWithFormat:@"%@" , idNumber];
    [AppUtils addActivityIndicator:self];
    NSString *serverurl = [[BASE_URL stringByAppendingString:DEL_ITEM ] stringByAppendingString:idString];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[AppUtils getUserToken] forHTTPHeaderField:@"x-access-token"];
    [manager DELETE:serverurl
         parameters:nil
            success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
                
                NSLog(@"Remove Item Success!");
                NSLog(@"JSON: %@", responseObject);
                [AppUtils removeActivityIndicator:self];
                
                mpage = 1;
                [self reloadData];
                
            } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
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
            }];
    

    
}
#pragma mark - UITableViewDelegate
- (void) tableView:(UITableView *)tableView didHighlightRowAtIndexPath:(NSIndexPath *)indexPath
{

}

- (void) tableView:(UITableView *)tableView didUnhighlightRowAtIndexPath:(NSIndexPath *)indexPath
{
    
}

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];

    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    MyItemDetailViewController *view = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"MyItemDetailViewId"];
    view.model = [self.myItems objectAtIndex:indexPath.row];
    view.mdismisCallBack = ^(NSInteger integerNum)
    {
        if (integerNum == 1)
        {
            mpage = 1;
            [self reloadData];
        }
    };
    [currentTopVC presentViewController:view animated:true completion:nil];
    
    
}
- (IBAction)onBtnSetLocationClick:(id)sender {
    
    
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    SetLocationViewController *controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SetLocationViewControllerId"];
    controller.mdismisCallBack = ^(NSInteger para)
    {
        mpage = 1;
        [self reloadData];
    };
    [currentTopVC presentViewController:controller animated:true completion:nil];
}


- (IBAction)onBtnAddNewItemClick:(id)sender {
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    AddNewItemViewController *controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"AddNewItemViewId"] ;
    controller.mdismisCallBack = ^(NSInteger param)
    {
        if (param == 1)
        {
            mpage = 1;
            [self reloadData];
        }
    };
    [currentTopVC presentViewController:controller animated:true completion:nil];
}
-(void)reloadData
{
    
    
    NSString *serverurl = [BASE_URL stringByAppendingString:GET_MY_ITEM_LIST ];
    
    NSDictionary *params = [self makeRequestParams];
    
    
    [self.mLblNoGoodies setHidden:true];
    [AppUtils addActivityIndicator:self];
    
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[AppUtils getUserToken] forHTTPHeaderField:@"x-access-token"];
    
    [manager POST:serverurl parameters:params progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        
        NSLog(@"JSON: %@", responseObject);
        if (mpage == 1)
        {
            [self.myItems removeAllObjects];
        }
        NSDictionary *receivedData = responseObject;
        NSArray *dataList = [receivedData objectForKey:@"data"];
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
            if (mpage > 1)
            {
                mpage = mpage - 1;
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
    NSString *keyword = self.txtSearch.text;
    NSNumber *m_page = [NSNumber numberWithInteger:mpage];
    NSNumber *m_limit = [NSNumber numberWithInteger:mlimit];
    
    NSDictionary *params = @{@"keyword":keyword, @"sort_type":[NSNumber numberWithInteger:0], @"direction_type":[NSNumber numberWithInteger:1] ,@"page":m_page ,@"limit":m_limit ,@"user_id":[NSNumber numberWithInteger:0] , @"user_id":[NSNumber numberWithInteger:0]};
    return params;
}

- (void)textFieldDidChange:(UITextField *)textField {
    NSLog(@"text changed: %@", textField.text);
    
//    if (textField.text.length > 2 || textField.text.length == 0)
//    {
        [self reloadData];
//    }
}


@end
