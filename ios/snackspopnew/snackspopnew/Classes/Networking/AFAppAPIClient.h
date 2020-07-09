//
//  AFAppAPIClient.h
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "AFNetworking.h"
#import "../Utils/AppUtils.h"
NS_ASSUME_NONNULL_BEGIN

@interface AFAppAPIClient : AFHTTPSessionManager
+ (instancetype)sharedInstance;

@end

NS_ASSUME_NONNULL_END
