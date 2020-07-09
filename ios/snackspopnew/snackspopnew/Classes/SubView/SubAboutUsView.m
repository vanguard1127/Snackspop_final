//
//  SubAboutUsView.m
//  snackspopnew
//
//  Created by Admin on 2019-04-10.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "SubAboutUsView.h"

@implementation SubAboutUsView

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/
- (void)awakeFromNib
{
    [super awakeFromNib];
    NSString *urlString = [BASE_SITE_URL stringByAppendingString:@"/aboutus"];
    NSURL *url = [NSURL URLWithString:urlString];
    NSURLRequest *urlRequest = [NSURLRequest requestWithURL:url];
    [_mWebView loadRequest:urlRequest];
}

@end
