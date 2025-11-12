import axios from 'axios';
const BASE_URL = 'http://localhost:8080';

const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const applyForCard = async (application: {
  userId: number;
  cardTypeId: number;
  requestLimit : number;
}) => {
  const response = await axios.post(
    `${BASE_URL}/card/application`,
    application,
    { headers: getAuthHeaders() }
  );
  return response.data;
};

export const fetchApplications = async () => {
  const response = await axios.get(
    `${BASE_URL}/card/application`,
    { headers: getAuthHeaders() }
  );
  return response.data;
};

export const updateApplicationStatus = async (
  id: number,
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
) => {
  const response = await axios.patch(
    `${BASE_URL}/card/application/${id}`,
    { status },
    { headers: getAuthHeaders() }
  );
  return response.data;
};

export const deleteApplication = async (id: number) => {
  const response = await axios.delete(
    `${BASE_URL}/card/application/${id}`,
    { headers: getAuthHeaders() }
  );
  return response.data;
};
