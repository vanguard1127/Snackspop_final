//
//  ImageCropperViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "ImageCropperViewController.h"

@interface ImageCropperViewController () <UINavigationControllerDelegate, UIImagePickerControllerDelegate>
@property (nonatomic ,assign) BOOL isSnaped;
@end

@implementation ImageCropperViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.isSnaped = NO;
    
    self.mBtnSnap.layer.masksToBounds = YES;
    self.mBtnSnap.layer.cornerRadius = 10;
    
    self.mBtnRotate.layer.masksToBounds = YES;
    self.mBtnRotate.layer.cornerRadius = 10;
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)onBtnCameraClick:(id)sender {
    NSLog(@"takePhoto");
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        UIImagePickerController *pickerViewController =[[UIImagePickerController alloc]init];
        pickerViewController.allowsEditing = YES;
        pickerViewController.delegate = self;
        pickerViewController.sourceType = UIImagePickerControllerSourceTypeCamera;
        [self presentViewController:pickerViewController animated:YES completion:nil];
    } else {
        UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"Error" message:@"Camera is not available" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction* defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {}];
        [alert addAction:defaultAction];
        [self presentViewController:alert animated:YES completion:nil];
    }
}

- (IBAction)onBtnGalleryClick:(id)sender {
    UIImagePickerController *pickerViewController = [[UIImagePickerController alloc] init];
    pickerViewController.allowsEditing = YES;
    pickerViewController.delegate = self;
    [pickerViewController setSourceType:UIImagePickerControllerSourceTypePhotoLibrary];
    [self presentViewController:pickerViewController animated:YES completion:nil];
}

- (IBAction)onBtnSaveClick:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
    if (self.didDismiss && self.image)
        self.didDismiss(self.image);
}

- (IBAction)onBtnCancel:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

- (IBAction)onBtnSnapClick:(id)sender {
    //TODO crop
    if (self.isSnaped == YES)
    {
        self.imageView.contentMode = UIViewContentModeScaleToFill;
    }
    else
    {
        self.imageView.contentMode = UIViewContentModeScaleAspectFit;
    }
    self.isSnaped = !self.isSnaped;
}

- (IBAction)onBtnRotateClick:(id)sender {
    if (self.image != nil)
    {
//        self.image = [AppUtils rotateUIImage:self.image                                clockwise:YES];
        UIImage *image = [AppUtils rotateUIImage:self.image                                clockwise:YES] ;
        if (image != nil)
        {
            self.image = image;
            self.imageView.image = image;
        }
    }
}

- (void) imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    [self dismissViewControllerAnimated:picker completion:nil];
    UIImage *image = [info valueForKey:UIImagePickerControllerEditedImage];
    self.image = image;
    self.imageView.image = image;
    //imageStatusLabel.text = @"Image imported into the app";
}
@end
