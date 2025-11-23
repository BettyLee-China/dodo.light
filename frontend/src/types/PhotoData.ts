 export type PhotoData = {
  title: string;
  objectName: string;
    description: string;
    price: number;
    photoMode:PhotoMode
};


export type PhotoMode = '风景' | '人像' | '夜景' | '微距' | '运动'|'美食';

export type Product={
  id:string;
  name:string;
  price:number;
  url:string;
  soldCount:number;
}