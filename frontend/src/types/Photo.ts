export interface Photo {
  id: string;
  title: string;
  description: string;
  price: number;
  url: string; 
  // 图片的 URL 或对象存储路径（如 OSS key）
}