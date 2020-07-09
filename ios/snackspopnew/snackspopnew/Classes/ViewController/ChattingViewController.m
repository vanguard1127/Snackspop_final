//
//  ChattingViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "ChattingViewController.h"
#import "KafkaRefresh.h"

@interface ChattingViewController ()
@property (readwrite, nonatomic, strong) NSMutableArray *myItems;
@property (readwrite, nonatomic, strong) NSMutableArray *deliverItems;
@property (readwrite, nonatomic) NSInteger page;
@property (readwrite , nonatomic) BOOL isTopRefreshed;
@property (readwrite , nonatomic) BOOL socketConnected;



@end
@implementation ChattingViewController
@synthesize model;

- (void)viewDidLoad {
    [super viewDidLoad];
    self.socketConnected = NO;
    // Do any additional setup after loading the view.
    self.tableView.rowHeight = UITableViewAutomaticDimension;
//    self.tableView.estimatedRowHeight = 105.5;
    self.mTextView.placeholder = @"Message";
    [self.tableView registerNib:[UINib nibWithNibName:@"raw_chatting_item_cell" bundle:nil]
         forCellReuseIdentifier:@"ChattinItemCellId"];
    
    [self.tableView registerNib:[UINib nibWithNibName:@"ChattingItemMyCellTableViewCell" bundle:nil]
         forCellReuseIdentifier:@"ChattingItemMyCellTableViewCellId"];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(didChangePreferredContentSize:)
                                                 name:UIContentSizeCategoryDidChangeNotification object:nil];
    
    _page = 1;
    self.isTopRefreshed = NO;

    [self.tableView bindGlobalStyleForHeadRefreshHandler:^{
        NSLog(@"header");
        self.page += 1;
        
        [self startAsyncGetMessages];
        self.isTopRefreshed = NO;
    }];
    
    
    _myItems = [[NSMutableArray alloc] init];
    _deliverItems = [[NSMutableArray alloc] init];
    

    
}


- (void)keyboardWillShow:(NSNotification *)notification
{
    CGRect frame = [notification.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
    CGRect newFrame = [self.view convertRect:frame fromView:[[UIApplication sharedApplication] delegate].window];




        self.topConstraint.constant = 0;
        self.bottomConstraint.constant = newFrame.size.height;
        [NSLayoutConstraint activateConstraints:[NSArray arrayWithObjects:
                                                 self.topConstraint, self.bottomConstraint, nil]];
        


    [self.view layoutIfNeeded];

}

- (void)keyboardWillHide:(NSNotification *)notification
{
    self.topConstraint.constant = 0;
    self.bottomConstraint.constant = 0;
    [NSLayoutConstraint activateConstraints:[NSArray arrayWithObjects:
                                                 self.topConstraint, self.bottomConstraint, nil]];
}
-(void)viewDidAppear:(BOOL)animated
{

}
-(void)refreshViewController:(ChatItemModel * )newModel{
    
    
    [_myItems removeAllObjects];
    [_deliverItems removeAllObjects];
    [self.tableView reloadData];
    self.model = newModel;
    self.lblTitle.text = [[self.model.user_first_name stringByAppendingString:@" "] stringByAppendingString:self.model.user_last_name];;
    
    
    
    self.topConstraint = [NSLayoutConstraint constraintWithItem:self.view
                                                      attribute:NSLayoutAttributeTop
                                                      relatedBy:NSLayoutRelationEqual
                                                         toItem:self.mTopView
                                                      attribute:NSLayoutAttributeTop
                                                     multiplier:1
                                                       constant:0];
    
    self.bottomConstraint = [NSLayoutConstraint constraintWithItem:self.view
                                                         attribute:NSLayoutAttributeBottom
                                                         relatedBy:NSLayoutRelationEqual
                                                            toItem:self.mTopView
                                                         attribute:NSLayoutAttributeBottom
                                                        multiplier:1
                                                          constant:0];
    
    [NSLayoutConstraint activateConstraints:[NSArray arrayWithObjects:
                                             self.topConstraint, self.bottomConstraint, nil]];
    
    [AppUtils setEnteredChatting:YES];
    if ((NSNull *)AppUtils.socketManager != [NSNull null] && self.socketConnected && [AppUtils.socketManager status] == SocketIOStatusConnected){
        [AppUtils.mSocket disconnect];
        self.socketConnected = NO;
    }
    NSURL* url = [[NSURL alloc] initWithString:BASE_SITE_URL];
    SocketManager* manager = [[SocketManager alloc] initWithSocketURL:url config:@{@"log": @YES, @"compress": @YES}];
    AppUtils.socketManager = manager;
    
    SocketIOClient* socket = manager.defaultSocket;
    AppUtils.mSocket = socket;
    
    __weak ChattingViewController *weekSelf = self;
    [AppUtils.mSocket on:@"connect" callback:^(NSArray* data, SocketAckEmitter* ack) {
        NSLog(@"socket connected");
        
        NSNumber *userid = [NSNumber numberWithInteger:[AppUtils getUserId]];
        NSNumber *itemId = [NSNumber numberWithInteger:self.model.itemId];
        NSNumber *toUserId = [NSNumber numberWithInteger:self.model.user_id];
        [AppUtils.mSocket emit:@"join" with:@[@{@"userId":userid , @"itemId":itemId , @"toUserId":toUserId}]];
    }];
    
    [AppUtils.mSocket on:@"joined" callback:^(NSArray* data, SocketAckEmitter* ack) {
        NSLog(@"socket joined");
        if (self.socketConnected == NO)
        {
            self.socketConnected = YES;
            self.page = 1;
            [self startAsyncGetMessages];
        }
    }];
    
    [AppUtils.mSocket on:@"receive" callback:^(NSArray* data, SocketAckEmitter* ack) {
        NSLog(@"socket received");
        NSDictionary *receiveddata = [data objectAtIndex:0];
        NSInteger chat_id = [[receiveddata objectForKey:@"id"] integerValue];
        NSNumber *chatNumber = [NSNumber numberWithInteger:chat_id];
        
        NSArray *dict =@[@{@"chat_id":chatNumber}];
        NSMutableArray *array = [[NSMutableArray alloc] init];
        [array addObject:dict];
        
        
        [AppUtils.mSocket emit:@"deliver"
                          with:array];
        [self addChatItem:receiveddata
                   isFlag:YES];
        
    }];
    
    [AppUtils.mSocket on:@"sent" callback:^(NSArray* data, SocketAckEmitter* ack) {
        NSLog(@"socket sent");
        NSDictionary *received = [data objectAtIndex: 0];
        [AppUtils removeActivityIndicator:weekSelf.view];
        [self addChatItem:received
                   isFlag:true];
        [self.mTextView setText:@""];
    }];
    [AppUtils.mSocket connect];
}
-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardDidHideNotification object:nil];
    
    self.isKeyboardShown = 0;
    
    [self refreshViewController:self.model];
}
-(void)viewWillDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [AppUtils setEnteredChatting:NO];
    [AppUtils.mSocket disconnect];
}
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:UIContentSizeCategoryDidChangeNotification
                                                  object:nil];
}

- (void)didChangePreferredContentSize:(NSNotification *)notification
{
    [self.tableView reloadData];
}
/*
- (CGFloat)tableView:(UITableView *)tableView estimatedHeightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return UITableViewAutomaticDimension;
}*/
- (IBAction)onBtnBack:(id)sender {
    if (self.mdismisCallBack != nil)
    {
        self.mdismisCallBack(1);
    }
    [AppUtils.mSocket disconnect];
    [self dismissViewControllerAnimated:YES completion:NULL];
}
- (IBAction)onBtnSend:(id)sender {
    
    NSString *message = self.mTextView.text;
    if (![AppUtils isTextEmpty:message])
    {
        NSNumber *myUserIdNum = [NSNumber numberWithInteger:[AppUtils getUserId]];
        NSNumber *toUserIdNum = [NSNumber numberWithInteger:self.model.user_id] ;
        NSNumber *itemIdNum = [NSNumber numberWithInteger:self.model.itemId];
        [AppUtils.mSocket emit:@"send"
                     with:@[@{@"fromUserId":myUserIdNum , @"toUserId":toUserIdNum , @"itemId" : itemIdNum ,@"message" : message}]];

        
        [AppUtils addActivityIndicator:self.view];
        
        
    }
}


- (IBAction)onBtnBackClick:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
}


- (NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.myItems count];
}
- (void)configureCell:(UITableViewCell *)cell1 forRowAtIndexPath:(NSIndexPath *)indexPath
{
    ChattingItemModel *model = [self.myItems objectAtIndex:indexPath.row];
    if ([cell1 isKindOfClass:[ChattingItemCell class]])
    {
        ChattingItemCell *cell = (ChattingItemCell *)cell1;
       
        [cell.leftImageUrl sd_setImageWithURL:[NSURL URLWithString:model.image1Url ]
                             placeholderImage:[UIImage imageNamed:@"ic_emptyuser"]];
        [cell.leftImageUrl setHidden:NO];
        
        cell.txtMessage.text = model.message;
        
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"YYYY-dd-MM HH:mm:ss"];
        NSString *dateString = [dateFormatter stringFromDate:model.date];
        cell.txtTime.text = dateString;
        
        cell.txtMessage.layer.cornerRadius = 5;
        cell.txtMessage.layer.masksToBounds = YES;
        [cell.txtMessage setBackgroundColor:[AppUtils colorWithHexString:@"0xC4EBC4"]];
    }
    else if([cell1 isKindOfClass:[ChattingItemMyCellTableViewCell class]])
    {
        ChattingItemMyCellTableViewCell *cell = (ChattingItemMyCellTableViewCell *)cell1;
        
        [cell.photoView sd_setImageWithURL:[NSURL URLWithString:model.image2Url ]
                          placeholderImage:[UIImage imageNamed:@"ic_emptyuser"]];
        
        cell.messageLabel.text = model.message;
        
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"YYYY-dd-MM HH:mm:ss"];
        NSString *dateString = [dateFormatter stringFromDate:model.date];
        cell.dateView.text = dateString;
        cell.messageLabel.layer.masksToBounds = YES;

        cell.messageLabel.layer.cornerRadius = 5;
        [cell.messageLabel setBackgroundColor:[AppUtils colorWithHexString:@"0xFFC79C"]];
    }
}
- (UITableViewCell*) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ChattingItemModel *model = [self.myItems objectAtIndex:indexPath.row];
    if (model.direction == 1)
    {
        static NSString *simpleTableIdentifier = @"ChattinItemCellId";
        
        ChattingItemCell *cell = (ChattingItemCell *)[tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
        if (cell == nil)
        {
            NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"raw_chatting_item_cell" owner:self options:nil];
            cell = [nib objectAtIndex:0];
        }
        [self configureCell:cell forRowAtIndexPath:indexPath];
        return cell;
    }
    else
    {
        static NSString *simpleTableIdentifier1 = @"ChattingItemMyCellTableViewCellId";
        ChattingItemMyCellTableViewCell *cell = (ChattingItemMyCellTableViewCell *)[tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier1];
        if (cell == nil)
        {
            NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"ChattingItemMyCellTableViewCell" owner:self options:nil];
            cell = [nib objectAtIndex:0];
        }
       [self configureCell:cell forRowAtIndexPath:indexPath];
        return cell;
    }
    
    
    
    
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return UITableViewAutomaticDimension;
    
  
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
    
    
}

-(void)startAsyncGetMessages
{
    
    
    NSString *serverurl = [BASE_URL stringByAppendingString:GET_CHAT_MESSAGE_LIST ];
    
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
        [self.deliverItems removeAllObjects];
        
        NSDictionary *receivedData = responseObject;
        NSArray *dataList = [receivedData objectForKey:@"data"];
        NSInteger count = dataList.count;
        if (count == 0)
        {
            self.page -= 1;
        }
        
        NSMutableArray *toDeliverItems = [[NSMutableArray alloc] init];
        for (int i = 0 ; i < dataList.count; i++)
        {
            
            NSDictionary *itemObject = [dataList objectAtIndex:i];
            [self addChatItem:itemObject
                       isFlag:NO];
            
            NSInteger deliver = [[itemObject objectForKey:@"deliver"] integerValue];
            NSInteger toUserId = [[itemObject objectForKey:@"to_user_id"] integerValue];
            NSNumber *chatId = [itemObject objectForKey:@"id"];
            if (deliver == 0 && toUserId == [AppUtils getUserId])
            {
                
                NSArray *dict =@[@{@"chat_id":chatId}];
                NSMutableArray *array = [[NSMutableArray alloc] init];
                [array addObject:dict];
//                NSArray *dict = @[@{@"chat_id":chatId}];
//                [toDeliverItems addObject:dict];
                [AppUtils.mSocket emit:@"deliver" with:array
                 ];
            }
            
            
        }
        self.isTopRefreshed = NO;
        
        [AppUtils removeActivityIndicator:self.view];

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

-(void) addChatItem :(NSDictionary *)object isFlag :(BOOL) value
{
    ChattingItemModel *model = [[ChattingItemModel alloc] init];
    NSInteger myUserId = [AppUtils getUserId];
    model.userid = self.model.user_id;
    model.image1Url = self.model.user_photo_id;
    model.image2Url = [AppUtils getUserImageUrl];
    NSInteger direction = 0;
    NSInteger fromUserId = [[object objectForKey:@"from_user_id"] integerValue];
//    NSInteger toUserId = [[object objectForKey:@"to_user_id"] integerValue];
    if (fromUserId == myUserId)
        direction = 0;
    else if (fromUserId == self.model.user_id)
    {
        direction = 1;
    }
    model.direction = direction;
    NSString *datestring = [object objectForKey:@"createdAt"];
    
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    // ignore +11 and use timezone name instead of seconds from gmt
    [dateFormat setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"];
    NSDate *dte = [dateFormat dateFromString:datestring];
    
    
    model.date = dte;
    
    model.message = [object objectForKey:@"message"];

    if (value == YES)
    {
        [self.myItems addObject:model];
    }
    else
    {
        [self.myItems insertObject:model
                           atIndex:0];
    }
    [self.tableView reloadData];
    if (!_isTopRefreshed && [self.myItems count] > 0)
    {
        NSInteger pos = [self.myItems count] - 1;
        NSIndexPath *path = [NSIndexPath indexPathForRow:pos inSection:0];
        [self.tableView scrollToRowAtIndexPath:path atScrollPosition:UITableViewScrollPositionTop animated:YES];
    }
}
-(NSDictionary *)makeRequestParams
{
    NSNumber *myUserIdNumber = [NSNumber numberWithInteger:[AppUtils getUserId]];
    NSNumber *otherUserIdNumber = [NSNumber numberWithInteger:self.model.user_id];
    NSNumber *itemId = [NSNumber numberWithInteger:self.model.itemId];
    NSNumber *pageNum = [NSNumber numberWithInteger:self.page];
    NSDictionary *params = @{@"fromUserId":otherUserIdNumber ,@"toUserId":myUserIdNumber , @"itemId":itemId , @"page" : pageNum};
    return params;
}


@end
