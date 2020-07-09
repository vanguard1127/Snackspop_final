//
//  ChatItemUserViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-07.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "ChatItemUserViewController.h"

@interface ChatItemUserViewController ()
@property (readwrite, nonatomic, strong) NSMutableArray *myItems;
@property (readwrite, nonatomic) NSInteger page;
@property (readwrite, nonatomic) NSInteger limit;
@end

@implementation ChatItemUserViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.tableView registerNib:[UINib nibWithNibName:@"raw_chat_item_cell" bundle:nil]
         forCellReuseIdentifier:@"ChatItemCellId"];
    
    _page = 1;
    _limit = 100;
    
    [self.tableView bindGlobalStyleForFootRefreshHandler:^{
        NSLog(@"footer");
        self.page +=1 ;
        [self startAsyncGetChatUserItem];
    }];
    [self.tableView bindGlobalStyleForHeadRefreshHandler:^{
        NSLog(@"header");
        self.page = 1;
        
        [self startAsyncGetChatUserItem];
    }];
    


    
    
    _myItems = [[NSMutableArray alloc] init];
    
    self.lblTitle.text = self.model.itemName;
    
    
    [self startAsyncGetChatUserItem];
    
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/


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
        
    static NSString *simpleTableIdentifier = @"ChatItemCellId";
    
    MyChatItemViewCell *cell = (MyChatItemViewCell *)[tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    if (cell == nil)
    {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"raw_chat_item_cell" owner:self options:nil];
        cell = [nib objectAtIndex:0];
    }
    cell.mFlag = 1;
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

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{

    
    ChattingViewController *view = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"ChattingViewId"];
    view.model = [self.myItems objectAtIndex:indexPath.row];
    view.mdismisCallBack = ^(NSInteger i){
        if (i == 1)
        {
            self.page = 1;
            [self startAsyncGetChatUserItem];
        }
    
    };
    [self presentViewController:view animated:YES completion:nil];
}
- (IBAction)onBtnBackClick:(id)sender {
    if (self.mdismisCallBack != nil)
    {
        self.mdismisCallBack(1);
    }
    [self dismissViewControllerAnimated:YES completion:NULL];
}

-(void)startAsyncGetChatUserItem
{
    
    
    NSString *serverurl = [BASE_URL stringByAppendingString:GET_CHAT_LIST_WITH_ITEM ];
    
    NSDictionary *params = [self makeRequestParams];
    [AppUtils addActivityIndicator:self.view];
    
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[AppUtils getUserToken] forHTTPHeaderField:@"x-access-token"];
    
    [manager POST:serverurl parameters:params progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        
        NSLog(@"JSON: %@", responseObject);
        if (self.page == 1)
        {
            [self.myItems removeAllObjects];
        }
        NSString *image_url = [BASE_URL stringByAppendingString:IMAGE_URL];
        
        NSDictionary *receivedData = responseObject;
        NSArray *dataList = [receivedData objectForKey:@"data"];
        for (int i = 0 ; i < dataList.count; i++)
        {
            NSDictionary *itemObject = [dataList objectAtIndex:i];
            ChatItemModel *model = [[ChatItemModel alloc] init];
            model.unreadCount = [[itemObject objectForKey:@"unread_cnt"] integerValue];
            NSDictionary *room = [itemObject objectForKey:@"room"];
            NSDictionary *item = [room objectForKey:@"item"];
            
            model.itemId = [[room objectForKey:@"item_id"] integerValue];
            model.room_from_user_id=[[room objectForKey:@"from_user_id"] integerValue];
            model.room_to_user_id = [[room objectForKey:@"to_user_id"] integerValue];
            model.item_user_id = [[item objectForKey:@"user_id"] integerValue];
            
            NSDictionary *user = [itemObject objectForKey:@"user"];
            model.user_first_name = [user objectForKey:@"first_name"];
            model.user_last_name = [user objectForKey:@"last_name"];
            model.user_id = [[user objectForKey:@"id"] integerValue];
        
            NSNumber *user_photo_id = [user objectForKey:@"photo"];
            model.user_photo_id = @"";
            if ((NSNull *)user_photo_id == [NSNull null])
            {
                
                model.user_photo_id = [image_url stringByAppendingString:[NSString stringWithFormat:@"%@" ,user_photo_id]];
            }
            NSArray *message = [room objectForKey:@"messages"];
            if (message.count > 0)
            {
                NSDictionary *msg =[message objectAtIndex:0];
                model.lastMessage =[msg objectForKey:@"message"];
            }
            
            NSArray *image = [item objectForKey:@"images"];
            if (image.count > 0)
            {
                NSDictionary *itemImageOject = [image objectAtIndex:0];
                NSNumber *image_id = [itemImageOject objectForKey:@"image"];
                
                
                model.item_image_id = [image_url stringByAppendingString:[NSString stringWithFormat:@"%@" ,image_id]];
            }
            [self.myItems addObject:model];
        }
        [AppUtils removeActivityIndicator:self.view];
        if ([self.myItems count] == 0)
        {
            if (self.page > 1)
            {
                self.page = self.page - 1;
            }
        }
        [self.tableView reloadData];
        [self.tableView.headRefreshControl endRefreshing];
        [self.tableView.footRefreshControl endRefreshing];
        
    } failure:^(NSURLSessionTask *operation, NSError *error) {
        NSLog(@"Error: %@", error);
        
        [AppUtils removeActivityIndicator:self.view];
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
    NSNumber *m_page = [NSNumber numberWithInteger:self.page];
    NSNumber *itemID = [NSNumber numberWithInteger:self.model.itemId];
    
    NSDictionary *params = @{@"page":m_page , @"item_id":itemID};
    return params;
}
@end
