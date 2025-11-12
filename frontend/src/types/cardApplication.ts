export interface CardApplication {
  id: number;
  userId: number;
  userName: string;
  cardTypeName: string;
  cardNetworkType: string;
  requestedLimit: number;
  applicationDate: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  reviewerName?: string;
  reviewDate?: string;
}

export interface UpdateApplicationStatusRequest {
  status: 'ACCEPTED' | 'REJECTED';
} 