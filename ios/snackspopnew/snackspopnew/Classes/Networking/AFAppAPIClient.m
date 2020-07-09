//
//  AFAppAPIClient.m
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "AFAppAPIClient.h"

@implementation AFAppAPIClient


+ (instancetype)sharedInstance {
    static AFAppAPIClient *_sharedClient = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _sharedClient = [[AFAppAPIClient alloc] initWithBaseURL:[NSURL URLWithString:BASE_SITE_URL]];
        _sharedClient.securityPolicy = [AFSecurityPolicy policyWithPinningMode:AFSSLPinningModeNone];
    });
    
    return _sharedClient;
}


@end
