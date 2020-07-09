//
//  MyItemModel.m
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "MyItemModel.h"
@interface MyItemModel ()

@end
@implementation MyItemModel

- (instancetype)initWithAttributes:(NSDictionary *)attributes {
    self = [super init];
    if (!self) {
        return nil;
    }
    
    self.itemId = (NSUInteger)[[attributes valueForKeyPath:@"id"] integerValue];
    
    return self;
}
@end

@implementation MyItemModel (NSCoding)

- (void)encodeWithCoder:(NSCoder *)aCoder {
    [aCoder encodeInteger:(NSInteger)self.itemId forKey:@"AF.userID"];
    
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super init];
    if (!self) {
        return nil;
    }
    
    self.itemId = (NSUInteger)[aDecoder decodeIntegerForKey:@"AF.userID"];
    
    
    return self;
}

+ (BOOL)supportsSecureCoding {
    return YES;
}

@end
