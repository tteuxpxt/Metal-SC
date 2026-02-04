import { render, screen } from '@testing-library/react';
import App from './App';

test('renders Metal-SC brand', () => {
  render(<App />);
  const brandElement = screen.getAllByText(/Metal-SC/i)[0];
  expect(brandElement).toBeInTheDocument();
});
