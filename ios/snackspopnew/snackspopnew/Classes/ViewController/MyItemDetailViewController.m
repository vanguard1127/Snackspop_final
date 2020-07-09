//
//  MyItemDetailViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-07.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "MyItemDetailViewController.h"

@interface MyItemDetailViewController ()

@end

@implementation MyItemDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    if (self.model != nil)
    {
        self.titleView.text = _model.itemName;
        self.nameView.text = _model.itemName;
        self.descriptionView.text = _model.itemDescription;
        NSString *price = [NSString stringWithFormat:@"%ld" ,_model.itemPrice];
        
        self.priceView.text = [price stringByAppendingString:_model.itemPriceUnit];;
        [self.imageView sd_setImageWithURL:[NSURL URLWithString:_model.item_image_id]
                          placeholderImage:[UIImage imageNamed:@"ic_logo_cir_red"]];
    }

}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/
- (void)setModel:(MyItemModel *)model{
    _model = model;
    

    
}
- (IBAction)onBtnEditClick:(id)sender {
    
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    AddNewItemViewController *view = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"AddNewItemViewId"];
    view.model = self.model;
    view.isUpdate = 1;
    view.mdismisCallBack = ^(NSInteger integer)
    {
        [self dismissViewControllerAnimated:YES completion:^{
            if (self.mdismisCallBack != nil)
            {
                self.mdismisCallBack(1);
            }
        }];
        
        
    };
    [currentTopVC presentViewController:view animated:true completion:nil];
    
}

- (IBAction)onBtnBackClick:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
}
@end
