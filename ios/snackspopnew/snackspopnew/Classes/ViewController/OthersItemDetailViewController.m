//
//  OthersItemDetailViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "OthersItemDetailViewController.h"
#import "../ItemViews/HorizontalTableViewCell.h"
#import "SignInViewController.h"
@interface OthersItemDetailViewController ()
@property (readwrite, nonatomic, strong) NSMutableArray *myItems;
@property (readwrite ,nonatomic ,copy)NSString *first_name;
@property (readwrite ,nonatomic ,copy)NSString *last_name;
@property (readwrite ,nonatomic ,copy)NSString *phone_number;
@property (readwrite ,nonatomic ,assign)NSInteger userId;
@end

@implementation OthersItemDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    _myItems = [[NSMutableArray alloc] init];
    
    
    self.itemTitleView.text = _model.itemName;
    NSString *price = [NSString stringWithFormat:@"%ld" ,_model.itemPrice];
    self.itemPriceView.text = [price stringByAppendingString:_model.itemPriceUnit];
    self.itemDescriptionView.text = _model.itemDescription;
    self.itemDistanceView.text = [NSString stringWithFormat:@"%.2fKm", _model.distance];

    self.otherIItemTile.text = _model.itemName;
    [self.itemImgView sd_setImageWithURL:[NSURL URLWithString:_model.item_image_id]
                        placeholderImage:[UIImage imageNamed:@"ic_logo_cir_red"]];
    
    [self.collectionView registerNib:[UINib nibWithNibName:@"raw_horizontal_item_cell" bundle:nil] forCellWithReuseIdentifier:@"HorizontalTableViewCellId"];
    [self startAsyncGetItemInfo];

    [self.collectionView setAllowsSelection:YES];
    [self.collectionView setAllowsMultipleSelection:NO];
    
//    [self.mBtnCall setEnabled:NO];
//    [self.mBtnMessage setEnabled:NO];
//    if ([AppUtils isLoggedIn])
//    {
//        [self.mBtnCall setEnabled:YES];
//        [self.mBtnMessage setEnabled:YES];
//    }
}
/*
- (void) viewDidLayoutSubviews
{
    NSLog(@"vidDidLayoutSubViews");

}
*/
- (void)setModel:(MyItemModel *)model{
    _model = model;

    
 
}
-(void)startAsyncGetItemInfo
{
   
    [_myItems removeAllObjects];
    NSInteger id1 = _model.itemId;
    NSString *str_id = [NSString stringWithFormat:@"%ld" ,id1];
    NSString *serverurl = [[BASE_URL stringByAppendingString:GET_ITEM] stringByAppendingString:str_id];
    
    NSDictionary *params = [self makeRequestParams];
    [AppUtils addActivityIndicator:self.view];
    
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[AppUtils getUserToken] forHTTPHeaderField:@"x-access-token"];
    
    [manager POST:serverurl parameters:params progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        
        NSLog(@"JSON: %@", responseObject);
        NSDictionary *receivedData = responseObject;
        NSDictionary *dataList = [receivedData objectForKey:@"data"];
        NSDictionary *item = [dataList objectForKey:@"item"];
        NSDictionary *user = [item objectForKey:@"user"];
        self.userId = [[item objectForKey:@"user_id"] integerValue];
        self.first_name = [user objectForKey:@"first_name"];
        NSString *mName= [self.first_name stringByAppendingString:@" "];
        self.last_name = [user objectForKey:@"last_name"];
        self.phone_number = [user objectForKey:@"phone_number"];
        NSString *user_name = [mName stringByAppendingString:self.last_name];
        
        double distance = [[user objectForKey:@"distance"] doubleValue];
        
        
        self.itemUsernameView.text = user_name;

        
        NSArray *items = [dataList objectForKey:@"items"];
        for (int i = 0 ; i < items.count; i++)
        {
            NSDictionary *itemObject = [items objectAtIndex:i];
            MyItemModel *model = [[MyItemModel alloc] init];
            
            model.itemName = [itemObject objectForKey:@"item_name"];
            model.itemId = [[itemObject objectForKey:@"id"] integerValue];
            NSString *itemDsc = @"Product Description:";
            itemDsc = [itemDsc stringByAppendingString:[itemObject objectForKey:@"item_description"]];
            model.itemDescription = itemDsc;
            model.itemPrice = [[itemObject objectForKey:@"item_price"] integerValue];
            model.itemPriceUnit =[itemObject objectForKey:@"item_unit"];
            model.userId = [[itemObject objectForKey:@"user_id"] integerValue];
            model.distance = distance;
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
        
        [AppUtils removeActivityIndicator:self.view];
        
        [self.collectionView reloadData];
    
        
        
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
        
    }];
}
-(NSDictionary *)makeRequestParams
{
    CLLocation *location = [AppUtils getUserLocation];
    NSNumber *lat = [NSNumber numberWithDouble:location.coordinate.latitude];
    NSNumber *lng = [NSNumber numberWithDouble:location.coordinate.longitude];
    
    NSDictionary *params = @{@"geo_lat" : lat , @"geo_lng":lng};
    //TODO geo location
    return params;
}
- (IBAction)onBtnCallClick:(id)sender {
    if ([AppUtils isLoggedIn])
    {
        NSInteger myUserId = [AppUtils getUserId];
        if (myUserId == self.userId)
        {
            [AppUtils showMessageWithOk:@"You cannot message or call yourself" withTitle:@"Alert"];
        }
        else
        {
            if (![AppUtils isTextEmpty:self.phone_number])
            {
                NSString *tel = @"tel:";
                tel = [tel stringByAppendingString:self.phone_number];
                [[UIApplication sharedApplication] openURL:[NSURL URLWithString:tel]];
            }
            else{
                [AppUtils showMessageWithOk:@"Phone Number is invalid." withTitle:@"Alert"];
            }
        }
        
    }
    else
    {
        
        dispatch_async(dispatch_get_main_queue(), ^{
            UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"Alert" message:@"You must log in First.Now Do You want to login?" preferredStyle:UIAlertControllerStyleAlert];
            [alertController addAction:[UIAlertAction actionWithTitle:@"Yes" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                
                UIViewController *currentTopVC = [AppUtils currentTopViewController];
                SignInViewController *signInViewController = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SignInViewId"];
                
                [currentTopVC presentViewController:signInViewController animated:true completion:nil];
                
                
            }]];
            [alertController addAction:[UIAlertAction actionWithTitle:@"No" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {

            }]];
            
            [[AppUtils currentTopViewController] presentViewController:alertController animated:YES completion:^{
            }];
        });
        
        
        
    }

}

- (IBAction)onBtnMessageClick:(id)sender {
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    if ([AppUtils isLoggedIn])
    {
        NSInteger myUserId = [AppUtils getUserId];
        if (myUserId == self.userId)
        {
            [AppUtils showMessageWithOk:@"You cannot message or call yourself" withTitle:@"Alert"];
        }
        else
        {
            ChattingViewController *view = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"ChattingViewId"];
            
            ChatItemModel *model = [[ChatItemModel alloc] init];
            model.user_id = self.model.userId;
            model.user_photo_id = self.model.user_image_url;
            model.user_last_name = self.last_name;
            model.user_first_name = self.first_name;
            model.itemId = self.model.itemId;
            view.model = model;
            
            [currentTopVC presentViewController:view animated:true completion:nil];
        }
        
        
    }
    else
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"Alert" message:@"You must log in First.Now Do You want to login?" preferredStyle:UIAlertControllerStyleAlert];
            [alertController addAction:[UIAlertAction actionWithTitle:@"Yes" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                
                UIViewController *currentTopVC = [AppUtils currentTopViewController];
                SignInViewController *signInViewController = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SignInViewId"];
                
                [currentTopVC presentViewController:signInViewController animated:true completion:nil];
                
                
            }]];
            [alertController addAction:[UIAlertAction actionWithTitle:@"No" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                
            }]];
            
            [[AppUtils currentTopViewController] presentViewController:alertController animated:YES completion:^{
            }];
        });
        
    }
    

}

- (IBAction)onBtnBackClick:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
}



- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return [self.myItems count];
}

// The cell that is returned must be retrieved from a call to -dequeueReusableCellWithReuseIdentifier:forIndexPath:
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    HorizontalTableViewCell *cell=[collectionView dequeueReusableCellWithReuseIdentifier:@"HorizontalTableViewCellId" forIndexPath:indexPath];
    
    
    if (cell == nil)
    {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"raw_horizontal_item_cell" owner:self options:nil];
        cell = [nib objectAtIndex:0];
    }
    
    //cell.backgroundColor=[UIColor greenColor];
    
    
    MyItemModel *model =[self.myItems objectAtIndex:indexPath.row];
    
    cell.model = model;

    
//    [cell setNeedsLayout];
    
    
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeMake(CGRectGetWidth(collectionView.frame) * 0.3, (CGRectGetHeight(collectionView.frame)));
}
-(void)updateViews :(MyItemModel *)selectedModel
{
    
    self.itemTitleView.text = selectedModel.itemName;
    [self.test1 setTitle:selectedModel.itemName forState:UIControlStateNormal];
    
    [self.otherIItemTile setText:selectedModel.itemName];
    [self.itemImgView sd_setImageWithURL:[NSURL URLWithString:selectedModel.item_image_id]
                        placeholderImage:[UIImage imageNamed:@"ic_logo_cir_red"]];
    
    NSString *price = [NSString stringWithFormat:@"%ld" ,selectedModel.itemPrice];
    self.itemPriceView.text = [price stringByAppendingString:selectedModel.itemPriceUnit];
    self.itemDescriptionView.text = selectedModel.itemDescription;
    self.itemDistanceView.text = [NSString stringWithFormat:@"%.2fKm", selectedModel.distance];
    
}
- (void)collectionView:(UICollectionView *)collectionView
didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"did select called");
    MyItemModel *selectedModel =[self.myItems objectAtIndex:indexPath.row];
    [self updateViews:selectedModel];
}


/*
- (void)collectionView:(UICollectionView *)collectionView didDeselectItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"did deselect called");

}
 */
@end

