//
//  AddNewItemViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "AddNewItemViewController.h"

@interface AddNewItemViewController ()
@property (assign ,nonatomic) BOOL isImageChanged;
@property (assign ,nonatomic) NSInteger mUploadedImageId;
@end

@implementation AddNewItemViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.itemDescription.placeholder = @"Project Description";
    
    self.isImageChanged = NO;
    
    self.itemPriceUnit.text = [AppUtils displayCurrencyInfoForLocale];
    if (self.model != nil)
    {
        
        self.titleView.text = _model.itemName;
        self.itemName.text = _model.itemName;
        self.itemDescription.text = _model.itemDescription;
        self.itemPriceUnit.text = _model.itemPriceUnit;
        NSString *price = [NSString stringWithFormat:@"%ld" ,_model.itemPrice];
        self.itemPrice.text = price;
        
        [self.itemImageView sd_setBackgroundImageWithURL:[NSURL URLWithString:_model.item_image_id] forState:UIControlStateNormal placeholderImage:[UIImage imageNamed:@"ic_logo_cir_red"]];
    }
}

-(void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
}
- (void)setModel:(MyItemModel *)model{
    _model = model;

}

- (IBAction)onBtnBack:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

- (IBAction)onBtnSave:(id)sender {
    NSLog(@"BtnSaveInClick");
    
    NSString *itemName = self.itemName.text;
    NSString *itemDescription = self.itemDescription.text;
    NSString *itemPrice = self.itemPrice.text;
    
    NSString *errorText = @"Please Enter";
    NSString *nameBlankText = @"  Item Name!";
    NSString *descriptionBlankText = @" Item Description!";
    NSString *priceBlankText = @" Item Price!";
    
    BOOL textError = NO;
    
    if ([AppUtils isTextEmpty:itemName])
    {
        errorText = [errorText stringByAppendingString:nameBlankText];
        textError = YES;
        [self.itemName becomeFirstResponder];
        
    }
    else if ([AppUtils isTextEmpty:itemDescription])
    {
        textError = YES;
        errorText = [errorText stringByAppendingString:descriptionBlankText];
        [self.itemDescription becomeFirstResponder];
    }
    else if ([AppUtils isTextEmpty:itemPrice])
    {
        textError = YES;
        errorText = [errorText stringByAppendingString:priceBlankText];
        [self.itemDescription becomeFirstResponder];
    }
    else
    {
        if (self.isImageChanged)
        {
            [self startAsyncUpload];
        }
        else
        {
            if (self.isUpdate == 1)
                [self startAsyncUpdate];
            else
                [self startAsyncCreate];
        }
    }
    if (textError) {
        [AppUtils showMessageWithOk:errorText withTitle:@"Alert"];
        return;
    }
}

- (IBAction)onBtnUpload:(id)sender {
    ImageCropperViewController *imageCropperView = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"ImageCropperViewId"];
    imageCropperView.didDismiss = ^(UIImage * _Nonnull data) {
        if (data != nil)
        {
            self.image = data;
            [self.itemImageView setBackgroundImage:data
                                          forState:UIControlStateNormal];
            self.isImageChanged = YES;
        }
    };
    [self presentViewController:imageCropperView animated:YES completion:nil];
}
-(void)startAsyncUpdate
{
    [AppUtils addActivityIndicator:self.view];
    NSString *serverurl = [BASE_URL stringByAppendingString:UPDATE_ITEM];
    NSString *idString = [NSString stringWithFormat:@"/%ld", self.model.itemId ];
    serverurl = [serverurl stringByAppendingString:idString];
    NSDictionary *params = [self makeRequestParams];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[AppUtils getUserToken] forHTTPHeaderField:@"x-access-token"];
    
    [manager PUT:serverurl
      parameters:params
         success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
             NSLog(@"Update Item Success!");
             
             [AppUtils removeActivityIndicator:self.view];
             dispatch_async(dispatch_get_main_queue(), ^{
                 // Update the UI on the main thread.
                 [self dismissViewControllerAnimated:YES completion: ^{
                     if (self.mdismisCallBack != nil)
                     {
                         self.mdismisCallBack(1);
                     }
                 }];
             });
             
         } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
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
-(void)startAsyncCreate
{
    [AppUtils addActivityIndicator:self.view];
    NSString *serverurl = [BASE_URL stringByAppendingString:CREATE_NEW_ITEM];
    
    NSDictionary *params = [self makeRequestParams];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[AppUtils getUserToken] forHTTPHeaderField:@"x-access-token"];
    
    
    [manager POST:serverurl parameters:params progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        NSLog(@"Create Item Success!");

        [AppUtils removeActivityIndicator:self.view];
       dispatch_async(dispatch_get_main_queue(), ^{
            // Update the UI on the main thread.
            [self dismissViewControllerAnimated:YES completion: ^{
                if (self.mdismisCallBack != nil)
                {
                    self.mdismisCallBack(1);
                }
            }];
       });
        
        
        

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
    NSMutableDictionary *mutableDict = [[NSMutableDictionary alloc]init];
    [mutableDict setObject:self.itemName.text forKey:@"item_name"];
    [mutableDict setObject:self.itemDescription.text forKey:@"item_description"];
    [mutableDict setObject:self.itemPrice.text forKey:@"item_price"];
    [mutableDict setObject:self.itemPriceUnit.text forKey:@"item_price_unit" ];
    
    
    if (self.isImageChanged)
    {
        NSDictionary *image = @{@"image":[NSNumber numberWithInteger:self.mUploadedImageId]};
        NSMutableArray *array = [[NSMutableArray alloc]init];
        [array addObject:image];
        [mutableDict setObject:array forKey:@"images"];
    }
    return mutableDict;
}

-(void)startAsyncUpload
{
    [AppUtils addActivityIndicator:self.view];
    NSString *serverurl = [BASE_URL stringByAppendingString:UPLOAD_FILE];
    UIImage *image = self.image;
    NSData *imageData = UIImageJPEGRepresentation(image,1);
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    
    [manager.requestSerializer setValue:@"multipart/form-data" forHTTPHeaderField:@"Content-Type"];
    
    [manager POST:serverurl parameters:nil constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
        [formData appendPartWithFileData:imageData
                                    name:@"imageupload"
                                fileName:@"item_image.jpg" mimeType:@"image/jpeg"];
        
        // etc.
    } progress:nil success:^(NSURLSessionDataTask *task, id responseObject) {
        
        NSLog(@"Upload Success!");
        NSLog(@"Response: %@", responseObject);
        
        NSString *str = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        NSLog(@"%@",str);
        NSError *jsonError;
        NSData *data = [str dataUsingEncoding:NSUTF8StringEncoding];
        NSDictionary * json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers  error:&jsonError];
        
 

        self.mUploadedImageId = [[json objectForKey:@"id"] integerValue];
        
        [AppUtils removeActivityIndicator:self.view];
        if (self.isUpdate == 1)
            [self startAsyncUpdate];
        else
            [self startAsyncCreate];
        
        
        
        
        
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
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

-(NSDictionary *)makeRequestUploadParams
{
    NSDictionary *param = @{};
    return param;
}
@end
