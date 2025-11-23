export interface OrderVO {
  orderId: string;
  orderStatus: 'PENDING_PAYMENT'|'PAID'|'CANCELLED';
  productName:string;
  quantity:number;
  unitPrice:number;
  createTime:Date;
  payTime:Date;
  imageUrl:string;
}