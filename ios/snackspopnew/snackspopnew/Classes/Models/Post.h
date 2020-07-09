//
//  Post.h
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MyItemModel.h"
#import "../Networking/AFAppAPIClient.h"
NS_ASSUME_NONNULL_BEGIN
@class MyItemModel;
@interface Post : NSObject
@property (nonatomic, assign) NSUInteger postID;
@property (nonatomic, strong) NSString *text;

@property (nonatomic, strong) MyItemModel *user;

- (instancetype)initWithAttributes:(NSDictionary *)attributes;

+ (NSURLSessionDataTask *)globalTimelinePostsWithBlock:(void (^)(NSArray *posts, NSError *error))block;

@end

NS_ASSUME_NONNULL_END
