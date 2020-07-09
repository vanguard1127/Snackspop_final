//
//  CreateNewAccountViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "CreateNewAccountViewController.h"

@interface CreateNewAccountViewController ()

@end

@implementation CreateNewAccountViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)onBtnBack:(id)sender {
    
    [self dismissViewControllerAnimated:YES completion:NULL];
}

- (IBAction)onBtnCreateAccountClick:(id)sender {
    
    NSLog(@"BtnSignUpClick");
    
    NSString *email = self.txtEmail.text;
    NSString *password = self.txtPassword.text;
    NSString *firstname = self.txtFirstName.text;
    NSString *lastName = self.txtLastName.text;
    
    NSString *errorText = @"Please Enter";
    NSString *usernameBlankText = @" a Email";
    NSString *passwordBlankText = @" a Password";
     NSString *firstnameBlankText = @" a FirstName";
     NSString *lastnameBlankText = @" a LastName";
    NSString *emailValite = @"Please Input Valid Email!";
    BOOL textError = NO;
    
    if ([AppUtils isTextEmpty:email])
    {
        errorText = [errorText stringByAppendingString:usernameBlankText];
        textError = YES;
        [self.txtEmail becomeFirstResponder];
        
    }
    else if (![AppUtils isValidEmail:email]){
        textError = YES;
        errorText = emailValite;
        [self.txtEmail becomeFirstResponder];
    }
    else if ([AppUtils isTextEmpty:password])
    {
        textError = YES;
        errorText = [errorText stringByAppendingString:passwordBlankText];
        [self.txtPassword becomeFirstResponder];
    }
    else if ([AppUtils isTextEmpty:firstname])
    {
        textError = YES;
        errorText = [errorText stringByAppendingString:firstnameBlankText];
        [self.txtFirstName becomeFirstResponder];
    }
    else if ([AppUtils isTextEmpty:lastName])
    {
        textError = YES;
        errorText = [errorText stringByAppendingString:lastnameBlankText];
        [self.txtLastName becomeFirstResponder];
    }
    else
    {
        [self startAsyncSingUp];
    }
    if (textError) {
        [AppUtils showMessageWithOk:errorText withTitle:@"Alert"];
        return;
    }
    
    
}

- (IBAction)onBtnTermsServiceClick:(id)sender {
    TermsViewController *homeView = [[UIStoryboard storyboardWithName:@"Main"  bundle:nil] instantiateViewControllerWithIdentifier:@"TermsViewId"];
    [self presentViewController:homeView animated:YES completion:nil];
}


-(void)startAsyncSingUp
{
    [AppUtils addActivityIndicator:self.view];
    NSString *serverurl = [BASE_URL stringByAppendingString:SIGNUP ];
    
    NSDictionary *params = [self makeRequestParams];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager POST:serverurl parameters:params progress:nil success:^(NSURLSessionTask *task, id responseObject) {
        NSLog(@"SignUp Success!");
        NSLog(@"JSON: %@", responseObject);
        
        [AppUtils saveUserInfo:responseObject];
        
        [AppUtils removeActivityIndicator:self.view];
        HomeViewController *homeView = [[UIStoryboard storyboardWithName:@"Main"  bundle:nil] instantiateViewControllerWithIdentifier:@"HomeViewId"];
        [self presentViewController:homeView animated:YES completion:nil];
        
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
    NSDictionary *params = @{@"email":self.txtEmail.text , @"password":self.txtPassword.text,
                             @"first_name":self.txtFirstName.text,
                             @"last_name":self.txtLastName.text,
                             @"phone_number":self.txtPhoneNum.text
                             };
    //TODO device type and id
    return params;
}


@end
