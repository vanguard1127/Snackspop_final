//
//  SubMyChatView.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "SubMyChatView.h"

@interface SubMyChatView()
@property (readwrite, nonatomic, strong) NSMutableArray *myItems;
@property (readwrite, nonatomic) NSInteger mpage;
@property (readwrite, nonatomic) NSInteger mlimit;
@end
@implementation SubMyChatView

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/
-(void)awakeFromNib{
    [super awakeFromNib];
    
    _mlimit = 100;
    _mpage = 1;
    [self.tableView bindGlobalStyleForFootRefreshHandler:^{
        NSLog(@"footer");
        self.mpage +=1 ;
        [self reloadData];
    }];
    [self.tableView bindGlobalStyleForHeadRefreshHandler:^{
        NSLog(@"header");
        self.mpage = 1;
        
        [self reloadData];
    }];
    
    [self.tableView registerNib:[UINib nibWithNibName:@"raw_chat_item_cell" bundle:nil]
         forCellReuseIdentifier:@"ChatItemCellId"];
    
    [self reloadData];
    
    _myItems = [[NSMutableArray alloc] init];
    
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
    
    static NSString *simpleTableIdentifier = @"ChatItemCellId";
    
    MyChatItemViewCell *cell = (MyChatItemViewCell *)[tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    if (cell == nil)
    {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"raw_chat_item_cell" owner:self options:nil];
        cell = [nib objectAtIndex:0];
    }
    cell.mFlag = 0;
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
    [tableView deselectRowAtIndexPath:indexPath animated:YES];

    //TODO callback
    ChatItemModel *model = [_myItems objectAtIndex:indexPath.row];
    NSInteger user_id = model.item_user_id;
    NSInteger from_user_id = model.room_from_user_id;
    NSInteger to_user_id = model.room_to_user_id;
    NSInteger myUserId = [AppUtils getUserId];
    if (from_user_id != myUserId)
    {
        to_user_id = from_user_id;
    }
    if (user_id != myUserId)
    {
        //ChattingActivity TODO
        UIViewController *currentTopVC = [AppUtils currentTopViewController];
        ChattingViewController *view = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"ChattingViewId"];
        view.model = model;
        view.mdismisCallBack = ^(NSInteger i)
        {
            if (i ==1)
            {
                self.mpage = 1;
                [self reloadData];
            }
        };
        [currentTopVC presentViewController:view animated:true completion:nil];
        
    }
    else
    {
        UIViewController *currentTopVC = [AppUtils currentTopViewController];
        ChatItemUserViewController *controller =[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"ChatItemUserViewId"];
        controller.model = model;
        controller.mdismisCallBack = ^(NSInteger i)
        {
            if (i ==1)
            {
                self.mpage = 1;
                [self reloadData];
            }
        };
        [currentTopVC presentViewController:controller animated:true completion:nil];
    }
 
}



-(void)reloadData
{
    
    
    NSString *serverurl = [BASE_URL stringByAppendingString:GET_ITEM_LIST_WITH_CHAT ];
    
    NSDictionary *params = [self makeRequestParams];
    [AppUtils addActivityIndicator:self];
    
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[AppUtils getUserToken] forHTTPHeaderField:@"x-access-token"];
    
    [manager POST:serverurl parameters:params progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        
        NSLog(@"JSON: %@", responseObject);
        if (self.mpage == 1)
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
           
            NSDictionary *user = [item objectForKey:@"user"];
            model.user_first_name = [user objectForKey:@"first_name"];
            model.user_last_name = [user objectForKey:@"last_name"];
            model.user_photo_id = @"";
            NSNumber *user_photo_id = [user objectForKey:@"photo"];
            if ((NSNull *)user_photo_id != [NSNull null])
            {
                
            model.user_photo_id = [image_url stringByAppendingString:[NSString stringWithFormat:@"%@" ,user_photo_id]];
            }
            model.itemName = [item objectForKey:@"item_name"];
            model.itemId = [[item objectForKey:@"id"] integerValue];
            model.room_from_user_id=[[room objectForKey:@"from_user_id"] integerValue];
            model.room_to_user_id = [[room objectForKey:@"to_user_id"] integerValue];
            model.item_user_id = [[item objectForKey:@"user_id"] integerValue];
            model.user_id = model.item_user_id;
            
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
        [AppUtils removeActivityIndicator:self];
        if ([self.myItems count] == 0)
        {
            if (self.mpage > 1)
            {
                self.mpage = self.mpage - 1;
            }
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
    NSNumber *m_page = [NSNumber numberWithInteger:self.mpage];
    NSNumber *m_limit = [NSNumber numberWithInteger:self.mlimit];
    
    NSDictionary *params = @{@"page":m_page ,@"limit":m_limit ,@"user_id":[NSNumber numberWithInteger:0]};
    return params;
}

@end
