// types/address.ts
export interface AddressVO {
  id: number;
  userId: number;
  receiverName: string;     // 收货人
  phone: string;         // 手机号
  province: string;      // 省
  city: string;          // 市
  district: string;      // 区
  detailAddress: string;        // 详细地址
  isDefault: boolean;    // 是否默认地址
  createTime?: Date;
  updateTime?: Date;
}